package org.example.novelplatform.controller;

import org.example.novelplatform.entity.User;
import org.example.novelplatform.service.AuthService;
import org.example.novelplatform.service.WechatAuthService;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wechat")
public class WechatLoginController {

    @Autowired
    private WechatAuthService wechatAuthService;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseMessage<Map<String, Object>> wechatLogin(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        String nickname = params.get("nickname");
        String avatarUrl = params.get("avatarUrl");

        return wechatAuthService.login(code, nickname, avatarUrl);
    }

    @PostMapping("/check-token")
    public ResponseMessage<User> checkToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return authService.checkToken(token);
    }
}
