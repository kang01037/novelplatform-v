package org.example.novelplatformv.service.impl;

import org.example.novelplatformv.entity.User;
import org.example.novelplatformv.mapper.UserMapper;
import org.example.novelplatformv.service.UserService;
import org.example.novelplatformv.util.PasswordEncoder;
import org.example.novelplatformv.util.ResponseMessage;
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
    public ResponseMessage<User> getUserById(Long userId) {
        User user = userMapper.selectByUserId(userId);
        if (user == null) {
            return ResponseMessage.error("用户不存在");
        }
        return ResponseMessage.success(user);
    }

    @Override
    public ResponseMessage<User> getUserByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return ResponseMessage.error("用户不存在");
        }
        return ResponseMessage.success(user);
    }

    @Override
    public ResponseMessage<List<User>> getAllUsers() {
        List<User> users = userMapper.selectAllUsers();
        return ResponseMessage.success(users);
    }

    @Override
    public ResponseMessage<String> registerUser(User user) {
        User existingUser = userMapper.selectByUsernameWithoutDeleted(user.getUsername());

        if (existingUser != null) {
            if (existingUser.getDeleted() == 1) {
                user.setUserId(existingUser.getUserId());

                String encodedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
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
                    return ResponseMessage.success("账号恢复成功");
                } else {
                    return ResponseMessage.error("账号恢复失败");
                }
            } else {
                return ResponseMessage.error("用户名已存在");
            }
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setUserStatus(user.getUserStatus() != null ? user.getUserStatus() : 1);
        user.setLoginCount(0);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        user.setDeleted(0);

        int result = userMapper.insertUser(user);
        if (result > 0) {
            return ResponseMessage.success("注册成功");
        } else {
            return ResponseMessage.error("注册失败");
        }
    }

    @Override
    public ResponseMessage<String> updateUser(User user) {
        user.setUpdatedTime(LocalDateTime.now());
        int result = userMapper.updateUser(user);
        if (result > 0) {
            return ResponseMessage.success("更新成功");
        } else {
            return ResponseMessage.error("更新失败");
        }
    }

    @Override
    public ResponseMessage<String> deleteUser(Long userId) {
        User user = userMapper.selectByUserId(userId);
        if (user == null) {
            return ResponseMessage.error("用户不存在");
        }

        user.setDeleted(1);
        user.setUpdatedTime(LocalDateTime.now());
        int result = userMapper.updateUser(user);

        if (result > 0) {
            return ResponseMessage.success("删除成功");
        } else {
            return ResponseMessage.error("删除失败");
        }
    }

    @Override
    public ResponseMessage<String> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return ResponseMessage.error("用户不存在");
        }

        if (user.getDeleted() == 1) {
            return ResponseMessage.error("账号已注销");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseMessage.error("密码错误");
        }

        if (user.getUserStatus() == 0) {
            return ResponseMessage.error("用户已被禁用");
        }

        updateLastLoginTime(user.getUserId());
        userMapper.incrementLoginCount(user.getUserId());
        user.setLastLoginTime(LocalDateTime.now());
        user.setPassword(null);

        return ResponseMessage.success("登录成功");
    }

    @Override
    public ResponseMessage<String> updateLastLoginTime(Long userId) {
        int result = userMapper.updateLastLoginTime(userId);
        if (result > 0) {
            return ResponseMessage.success("更新时间成功");
        } else {
            return ResponseMessage.error("更新时间失败");
        }
    }

    @Override
    public ResponseMessage<String> uploadAvatar(Long userId, MultipartFile file) {
        User user = userMapper.selectByUserId(userId);
        if (user == null) {
            return ResponseMessage.error("用户不存在");
        }

        if (file.isEmpty()) {
            return ResponseMessage.error("上传文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches(".*\\.(jpg|jpeg|png|gif|webp)$")) {
            return ResponseMessage.error("只支持 jpg、jpeg、png、gif、webp 格式的图片");
        }

        long fileSize = file.getSize();
        if (fileSize > 5 * 1024 * 1024) {
            return ResponseMessage.error("文件大小不能超过 5MB");
        }
        //会存储到：D:\idea2025\ novelplatform-v\ uploads\ avatars\ 目录下
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
                return ResponseMessage.success("头像上传成功");
            } else {
                Files.deleteIfExists(filePath);
                return ResponseMessage.error("头像上传失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseMessage.error("头像上传失败：" + e.getMessage());
        }
    }
}
