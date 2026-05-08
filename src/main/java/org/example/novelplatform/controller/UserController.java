package org.example.novelplatform.controller;

import org.example.novelplatform.entity.User;
import org.example.novelplatform.service.AuthService;
import org.example.novelplatform.service.FileService;
import org.example.novelplatform.service.UserService;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final FileService fileService;

    public UserController(UserService userService, AuthService authService, FileService fileService) {
        this.userService = userService;
        this.authService = authService;
        this.fileService = fileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseMessage<User>> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(ResponseMessage.success(user));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ResponseMessage<User>> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ResponseMessage.success(user));
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseMessage<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ResponseMessage.success(users));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage<User>> register(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(ResponseMessage.success("注册成功", registeredUser));
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage<User>> updateUser(@RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(ResponseMessage.success("更新成功", updatedUser));
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ResponseMessage<Void>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ResponseMessage.success("删除成功", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseMessage<Map<String, Object>>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Map<String, Object> loginData_result = authService.login(username, password);
        return ResponseEntity.ok(ResponseMessage.success("登录成功", loginData_result));
    }

    @PostMapping("/upload/avatar")
    public ResponseEntity<ResponseMessage<String>> uploadAvatar(
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadAvatar(userId, file);
        return ResponseEntity.ok(ResponseMessage.success("头像上传成功", avatarUrl));
    }

    @GetMapping("/avatar/{filename}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable String filename) {
        return fileService.getAvatar(filename);
    }

    @DeleteMapping("/avatar/{userId}")
    public ResponseEntity<ResponseMessage<Void>> deleteAvatar(@PathVariable Long userId) {
        userService.deleteAvatar(userId);
        return ResponseEntity.ok(ResponseMessage.success("头像删除成功", null));
    }
}
