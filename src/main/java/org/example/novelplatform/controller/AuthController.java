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

        String tokenHash = md5(refreshToken);
        String redisKey = "refresh:token:" + tokenHash;

        // 检查 Redis 中是否存在（防止已被吊销）
        if (!redisUtil.hasKey(redisKey)) {
            return ResponseMessage.error("refreshToken 已被吊销");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String username = jwtUtil.getUsernameFromToken(refreshToken);

        // 删除旧 refresh token（轮换机制）
        redisUtil.delete(redisKey);

        // 签发新 token 对
        String newAccessToken = jwtUtil.generateAccessToken(userId, username);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, username);

        // 新 refresh token 存入 Redis
        String newTokenHash = md5(newRefreshToken);
        redisUtil.set("refresh:token:" + newTokenHash, String.valueOf(userId),
                jwtUtil.getRefreshExpiration() / 1000);

        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", newAccessToken);
        data.put("refreshToken", newRefreshToken);

        return ResponseMessage.success("刷新成功", data);
    }

    @PostMapping("/logout")
    public ResponseMessage<String> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken != null) {
            String tokenHash = md5(refreshToken);
            redisUtil.delete("refresh:token:" + tokenHash);
        }
        return ResponseMessage.success("已退出登录");
    }

    private String md5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }
}
