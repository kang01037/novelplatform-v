package org.example.novelplatform.service;

import org.example.novelplatform.entity.User;

import java.util.Map;

public interface AuthService {

    Map<String, Object> login(String username, String password);

    void logout(String refreshToken);

    Map<String, Object> refreshToken(String refreshToken);

    Map<String, Object> generateTokenPair(User user);

    User checkToken(String token);
}
