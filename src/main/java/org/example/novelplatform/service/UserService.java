package org.example.novelplatform.service;

import org.example.novelplatform.entity.User;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    ResponseMessage<User> getUserById(Long userId);

    ResponseMessage<User> getUserByUsername(String username);

    User getUserByOpenid(String openid);

    ResponseMessage<List<User>> getAllUsers();

    ResponseMessage<String> registerUser(User user);

    ResponseMessage<String> updateUser(User user);

    ResponseMessage<String> deleteUser(Long userId);

    ResponseMessage<String> login(String username, String password);

    ResponseMessage<String> updateLastLoginTime(Long userId);

    ResponseMessage<String> uploadAvatar(Long userId, MultipartFile file);

    ResponseMessage<String> deleteAvatar(Long userId);

}
