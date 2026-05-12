package org.example.novelplatform.service;

import org.example.novelplatform.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    User getUserById(Long userId);

    User getUserByUsername(String username);

    User getUserByOpenid(String openid);

    List<User> getAllUsers();

    User registerUser(User user);

    User updateUser(User user);

    void deleteUser(Long userId);

    User login(String username, String password);

    void updateLastLoginTime(Long userId);

    String uploadAvatar(Long userId, MultipartFile file);

    void deleteAvatar(Long userId);
}
