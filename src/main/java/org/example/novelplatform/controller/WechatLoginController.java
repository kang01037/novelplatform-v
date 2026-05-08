package org.example.novelplatform.controller;

import org.example.novelplatform.entity.User;
import org.example.novelplatform.service.AuthService;
import org.example.novelplatform.service.WechatAuthService;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wechat")
public class WechatLoginController {

    private final WechatAuthService wechatAuthService;
    private final AuthService authService;

    public WechatLoginController(WechatAuthService wechatAuthService, AuthService authService) {
        this.wechatAuthService = wechatAuthService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseMessage<Map<String, Object>>> wechatLogin(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        String nickname = params.get("nickname");
        String avatarUrl = params.get("avatarUrl");

        Map<String, Object> loginData = wechatAuthService.login(code, nickname, avatarUrl);
        return ResponseEntity.ok(ResponseMessage.success("登录成功", loginData));
    }

    @PostMapping("/check-token")
    public ResponseEntity<ResponseMessage<User>> checkToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        User user = authService.checkToken(token);
        return ResponseEntity.ok(ResponseMessage.success(user));
    }
}
