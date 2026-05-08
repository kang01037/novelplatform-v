package org.example.novelplatform.controller;

import org.example.novelplatform.service.AuthService;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseMessage<Map<String, Object>>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        Map<String, Object> tokenData = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ResponseMessage.success("刷新成功", tokenData));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseMessage<Void>> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        authService.logout(refreshToken);
        return ResponseEntity.ok(ResponseMessage.success("登出成功", null));
    }
}
