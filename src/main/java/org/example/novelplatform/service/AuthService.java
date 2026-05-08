package org.example.novelplatform.service;

import org.example.novelplatform.entity.User;
import org.example.novelplatform.util.ResponseMessage;

import java.util.Map;

public interface AuthService {

    ResponseMessage<Map<String, Object>> login(String username, String password);

    ResponseMessage<String> logout(String refreshToken);

    ResponseMessage<Map<String, Object>> refreshToken(String refreshToken);

    ResponseMessage<Map<String, Object>> generateTokenPair(User user);

    ResponseMessage<User> checkToken(String token);
}
