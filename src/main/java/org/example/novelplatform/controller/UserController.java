package org.example.novelplatform.controller;

import org.example.novelplatform.entity.User;
import org.example.novelplatform.service.AuthService;
import org.example.novelplatform.service.FileService;
import org.example.novelplatform.service.UserService;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private FileService fileService;

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseMessage<User>> getUserById(@PathVariable Long userId) {
        ResponseMessage<User> response = userService.getUserById(userId);
        if (response.getData() == null) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ResponseMessage<User>> getUserByUsername(@PathVariable String username) {
        ResponseMessage<User> response = userService.getUserByUsername(username);
        if (response.getData() == null) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseMessage<List<User>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage<String>> register(@RequestBody User user) {
        ResponseMessage<String> response = userService.registerUser(user);
        if ("注册成功".equals(response.getData())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage<String>> updateUser(@RequestBody User user) {
        ResponseMessage<String> response = userService.updateUser(user);
        if ("更新成功".equals(response.getData())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ResponseMessage<String>> deleteUser(@PathVariable Long userId) {
        ResponseMessage<String> response = userService.deleteUser(userId);
        if ("删除成功".equals(response.getData())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseMessage<Map<String, Object>>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        ResponseMessage<Map<String, Object>> response = authService.login(username, password);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/upload/avatar")
    public ResponseEntity<ResponseMessage<String>> uploadAvatar(
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file) {
        ResponseMessage<String> response = userService.uploadAvatar(userId, file);
        if ("头像上传成功".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/avatar/{filename}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable String filename) {
        return fileService.getAvatar(filename);
    }

    @DeleteMapping("/avatar/{userId}")
    public ResponseEntity<ResponseMessage<String>> deleteAvatar(@PathVariable Long userId) {
        ResponseMessage<String> response = userService.deleteAvatar(userId);
        if ("头像删除成功".equals(response.getData()) || "头像不存在".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
