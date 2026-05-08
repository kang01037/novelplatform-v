package org.example.novelplatform.controller;

import org.example.novelplatform.util.JwtUtil;
import org.example.novelplatform.util.RedisUtil;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public AuthController(JwtUtil jwtUtil, RedisUtil redisUtil) {
        this.jwtUtil = jwtUtil;
        this.redisUtil = redisUtil;
    }

    @PostMapping("/refresh")
    public ResponseMessage<Map<String, Object>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseMessage.error("缺少 refreshToken");
        }

        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            return ResponseMessage.error("refreshToken 无效或已过期");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String redisKey = "refresh:token:" + userId;

        if (!redisUtil.hasKey(redisKey)) {
            return ResponseMessage.error("refreshToken 已被吊销");
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);

        redisUtil.delete(redisKey);

        String newAccessToken = jwtUtil.generateAccessToken(userId, username);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, username);

        redisUtil.set("refresh:token:" + userId, String.valueOf(userId),
                jwtUtil.getRefreshExpiration() / 1000);

        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", newAccessToken);
        data.put("refreshToken", newRefreshToken);

        return ResponseMessage.success("刷新成功", data);
    }

    @PostMapping("/logout")
    public ResponseMessage<String> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken != null && jwtUtil.validateRefreshToken(refreshToken)) {
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            redisUtil.delete("refresh:token:" + userId);
        }
        return ResponseMessage.success("已退出登录");
    }
}
