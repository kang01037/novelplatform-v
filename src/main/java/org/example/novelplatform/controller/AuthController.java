package org.example.novelplatform.controller;

import org.example.novelplatform.service.AuthService;
import org.example.novelplatform.util.ResponseMessage;
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
    public ResponseMessage<Map<String, Object>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseMessage<String> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return authService.logout(refreshToken);
    }
}
