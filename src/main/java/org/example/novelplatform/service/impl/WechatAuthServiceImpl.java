package org.example.novelplatform.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.novelplatform.config.WechatMiniAppConfig;
import org.example.novelplatform.entity.User;
import org.example.novelplatform.exception.ServiceException;
import org.example.novelplatform.service.AuthService;
import org.example.novelplatform.service.UserService;
import org.example.novelplatform.service.WechatAuthService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WechatAuthServiceImpl implements WechatAuthService {

    private final WechatMiniAppConfig wechatConfig;
    private final UserService userService;
    private final AuthService authService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WechatAuthServiceImpl(WechatMiniAppConfig wechatConfig,
                                 UserService userService,
                                 AuthService authService) {
        this.wechatConfig = wechatConfig;
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    public Map<String, Object> login(String code, String nickname, String avatarUrl) {
        if (code == null || code.isEmpty()) {
            throw new ServiceException("登录码不能为空");
        }

        try {
            String url = String.format(
                    "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    wechatConfig.getAppId(),
                    wechatConfig.getAppSecret(),
                    code
            );

            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has("errcode")) {
                throw new ServiceException("微信登录失败: " + jsonNode.get("errmsg").asText());
            }

            String openid = jsonNode.get("openid").asText();

            User user = userService.getUserByOpenid(openid);

            if (user == null) {
                user = new User();
                user.setUsername("wx_" + openid.substring(0, 8));
                user.setNickname(nickname != null ? nickname : "微信用户");
                user.setAvatar(avatarUrl);
                user.setWechatOpenid(openid);
                user.setUserStatus(1);
                userService.registerUser(user);
                user = userService.getUserByOpenid(openid);
            }

            return authService.generateTokenPair(user);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("系统错误，请稍后重试");
        }
    }
}
