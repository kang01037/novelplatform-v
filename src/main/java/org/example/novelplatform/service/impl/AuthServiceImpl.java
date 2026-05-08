package org.example.novelplatform.service.impl;

import org.example.novelplatform.entity.User;
import org.example.novelplatform.exception.ServiceException;
import org.example.novelplatform.service.AuthService;
import org.example.novelplatform.service.UserService;
import org.example.novelplatform.util.JwtUtil;
import org.example.novelplatform.util.RedisUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public AuthServiceImpl(UserService userService, JwtUtil jwtUtil, RedisUtil redisUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.redisUtil = redisUtil;
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        userService.login(username, password);

        User user = userService.getUserByUsername(username);
        return generateTokenPair(user);
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken != null && jwtUtil.validateRefreshToken(refreshToken)) {
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            redisUtil.delete("refresh:token:" + userId);
        }
    }

    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new ServiceException("缺少 refreshToken");
        }

        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new ServiceException("refreshToken 无效或已过期");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String redisKey = "refresh:token:" + userId;

        if (!redisUtil.hasKey(redisKey)) {
            throw new ServiceException("refreshToken 已被吊销");
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

        return data;
    }

    @Override
    public Map<String, Object> generateTokenPair(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(), user.getUsername());

        redisUtil.set("refresh:token:" + user.getUserId(), String.valueOf(user.getUserId()),
                jwtUtil.getRefreshExpiration() / 1000);

        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);
        data.put("userInfo", user);

        return data;
    }

    @Override
    public User checkToken(String token) {
        if (token == null || !jwtUtil.validateAccessToken(token)) {
            throw new ServiceException("Token无效或已过期");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        return userService.getUserById(userId);
    }
}
