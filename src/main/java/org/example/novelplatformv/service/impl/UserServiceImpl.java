package org.example.novelplatformv.service.impl;

import org.example.novelplatformv.entity.User;
import org.example.novelplatformv.mapper.UserMapper;
import org.example.novelplatformv.service.UserService;
import org.example.novelplatformv.util.PasswordEncoder;
import org.example.novelplatformv.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
                user.setUserStatus(1);
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
        user.setUserStatus(1);
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

        if (user.getUserStatus() != 1) {
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
}
