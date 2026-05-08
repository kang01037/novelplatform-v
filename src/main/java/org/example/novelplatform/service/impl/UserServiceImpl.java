package org.example.novelplatform.service.impl;

import org.example.novelplatform.entity.User;
import org.example.novelplatform.exception.ServiceException;
import org.example.novelplatform.mapper.UserMapper;
import org.example.novelplatform.service.UserService;
import org.example.novelplatform.util.FileValidator;
import org.example.novelplatform.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(Long userId) {
        User user = userMapper.selectByUserId(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        return user;
    }

    @Override
    public User getUserByOpenid(String openid) {
        return userMapper.selectByOpenid(openid);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAllUsers();
    }

    @Override
    public User registerUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        } else {
            user.setPassword(null);
        }

        User existingUser = userMapper.selectByUsernameWithoutDeleted(user.getUsername());

        if (existingUser != null) {
            if (existingUser.getDeleted() == 1) {
                user.setUserId(existingUser.getUserId());
                user.setEmail(user.getEmail());
                user.setPhone(user.getPhone());
                user.setAvatar(user.getAvatar());
                user.setNickname(user.getNickname());
                user.setGender(user.getGender());
                user.setBirthday(user.getBirthday());
                user.setUserStatus(user.getUserStatus() != null ? user.getUserStatus() : 1);
                user.setLoginCount(0);
                user.setLastLoginTime(null);
                user.setCreatedTime(existingUser.getCreatedTime());
                user.setUpdatedTime(LocalDateTime.now());
                user.setDeleted(0);

                int result = userMapper.updateUser(user);
                if (result > 0) {
                    return userMapper.selectByUserId(user.getUserId());
                } else {
                    throw new ServiceException("账号恢复失败");
                }
            } else {
                throw new ServiceException("用户名已存在");
            }
        }

        user.setUserStatus(user.getUserStatus() != null ? user.getUserStatus() : 1);
        user.setLoginCount(0);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        user.setDeleted(0);

        int result = userMapper.insertUser(user);
        if (result > 0) {
            return userMapper.selectByUsername(user.getUsername());
        } else {
            throw new ServiceException("注册失败");
        }
    }

    @Override
    public User updateUser(User user) {
        user.setUpdatedTime(LocalDateTime.now());
        int result = userMapper.updateUser(user);
        if (result > 0) {
            return userMapper.selectByUserId(user.getUserId());
        } else {
            throw new ServiceException("更新失败");
        }
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userMapper.selectByUserId(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        user.setDeleted(1);
        user.setUpdatedTime(LocalDateTime.now());
        int result = userMapper.updateUser(user);

        if (result <= 0) {
            throw new ServiceException("删除失败");
        }
    }

    @Override
    public void login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        if (user.getDeleted() == 1) {
            throw new ServiceException("账号已注销");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ServiceException("密码错误");
        }

        if (user.getUserStatus() == 0) {
            throw new ServiceException("用户已被禁用");
        }

        updateLastLoginTime(user.getUserId());
        userMapper.incrementLoginCount(user.getUserId());
        user.setLastLoginTime(LocalDateTime.now());
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        int result = userMapper.updateLastLoginTime(userId);
        if (result <= 0) {
            throw new ServiceException("更新时间失败");
        }
    }

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        User user = userMapper.selectByUserId(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        if (file.isEmpty()) {
            throw new ServiceException("上传文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches(".*\\.(jpg|jpeg|png|gif|webp)$")) {
            throw new ServiceException("只支持 jpg、jpeg、png、gif、webp 格式的图片");
        }

        if (!FileValidator.validateImageFile(file)) {
            throw new ServiceException("文件内容校验失败，请上传有效的图片文件");
        }

        long fileSize = file.getSize();
        if (fileSize > 5 * 1024 * 1024) {
            throw new ServiceException("文件大小不能超过 5MB");
        }

        String uploadDir = System.getProperty("user.dir") + "/uploads/avatars/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;
        Path filePath = Paths.get(uploadDir + newFilename);

        try {
            Files.write(filePath, file.getBytes());

            String avatarUrl = "/api/user/avatar/" + newFilename;
            user.setAvatar(avatarUrl);
            user.setUpdatedTime(LocalDateTime.now());

            int result = userMapper.updateUser(user);
            if (result > 0) {
                return avatarUrl;
            } else {
                Files.deleteIfExists(filePath);
                throw new ServiceException("头像上传失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException("头像上传失败");
        }
    }

    @Override
    public void deleteAvatar(Long userId) {
        User user = userMapper.selectByUserId(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        String avatar = user.getAvatar();
        if (avatar == null || avatar.isEmpty()) {
            return;
        }

        String uploadDir = System.getProperty("user.dir") + "/uploads/avatars/";
        String filename = avatar.substring(avatar.lastIndexOf("/") + 1);
        Path filePath = Paths.get(uploadDir + filename);

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            user.setAvatar(null);
            user.setUpdatedTime(LocalDateTime.now());

            int result = userMapper.updateUser(user);
            if (result <= 0) {
                throw new ServiceException("头像删除失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException("头像删除失败");
        }
    }
}
