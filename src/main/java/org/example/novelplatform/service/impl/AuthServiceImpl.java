package org.example.novelplatform.service.impl;

import org.example.novelplatform.entity.User;
import org.example.novelplatform.service.AuthService;
import org.example.novelplatform.service.UserService;
import org.example.novelplatform.util.JwtUtil;
import org.example.novelplatform.util.RedisUtil;
import org.example.novelplatform.util.ResponseMessage;
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
    public ResponseMessage<Map<String, Object>> login(String username, String password) {
        ResponseMessage<String> loginResult = userService.login(username, password);
        if (loginResult.getCode() != 200) {
            return ResponseMessage.error(loginResult.getMessage());
        }

        ResponseMessage<User> userResponse = userService.getUserByUsername(username);
        User user = userResponse.getData();
        if (user == null) {
            return ResponseMessage.error("用户不存在");
        }

        return generateTokenPair(user);
    }

    @Override
    public ResponseMessage<String> logout(String refreshToken) {
        if (refreshToken != null && jwtUtil.validateRefreshToken(refreshToken)) {
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            redisUtil.delete("refresh:token:" + userId);
        }
        return ResponseMessage.success("已退出登录", "已退出登录");
    }

    @Override
    public ResponseMessage<Map<String, Object>> refreshToken(String refreshToken) {
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

    @Override
    public ResponseMessage<Map<String, Object>> generateTokenPair(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(), user.getUsername());

        redisUtil.set("refresh:token:" + user.getUserId(), String.valueOf(user.getUserId()),
                jwtUtil.getRefreshExpiration() / 1000);

        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);
        data.put("userInfo", user);

        return ResponseMessage.success("登录成功", data);
    }

    @Override
    public ResponseMessage<User> checkToken(String token) {
        if (token != null && jwtUtil.validateAccessToken(token)) {
            Long userId = jwtUtil.getUserIdFromToken(token);
            User user = userService.getUserById(userId).getData();
            return ResponseMessage.success("Token有效", user);
        } else {
            return ResponseMessage.error("Token无效或已过期");
        }
    }
}
