package org.example.novelplatform.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.novelplatform.config.WechatMiniAppConfig;
import org.example.novelplatform.entity.User;
import org.example.novelplatform.service.UserService;
import org.example.novelplatform.util.JwtUtil;
import org.example.novelplatform.util.RedisUtil;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wechat")
public class WechatLoginController {

    @Autowired
    private WechatMiniAppConfig wechatConfig;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/login")
    public ResponseMessage wechatLogin(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        String nickname = params.get("nickname");
        String avatarUrl = params.get("avatarUrl");

        if (code == null || code.isEmpty()) {
            return ResponseMessage.error("登录码不能为空");
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
                return ResponseMessage.error("微信登录失败: " + jsonNode.get("errmsg").asText());
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
            }

            String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(), user.getUsername());

            redisUtil.set("refresh:token:" + user.getUserId(), String.valueOf(user.getUserId()),
                    jwtUtil.getRefreshExpiration() / 1000);

            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", accessToken);
            data.put("refreshToken", refreshToken);
            data.put("userInfo", user);

            return ResponseMessage.success("登录成功", data);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.error("系统错误，请稍后重试");
        }
    }

    @PostMapping("/check-token")
    public ResponseMessage<User> checkToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token != null && jwtUtil.validateAccessToken(token)) {
            Long userId = jwtUtil.getUserIdFromToken(token);
            User user = userService.getUserById(userId).getData();
            return ResponseMessage.success("Token有效", user);
        } else {
            return ResponseMessage.error("Token无效或已过期");
        }
    }
}
