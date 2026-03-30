package org.example.novelplatformv.controller;

import org.example.novelplatformv.entity.User;
import org.example.novelplatformv.service.UserService;
import org.example.novelplatformv.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

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
    public ResponseEntity<ResponseMessage<java.util.List<User>>> getAllUsers() {
        ResponseMessage<List<User>> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
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
    public ResponseEntity<ResponseMessage<String>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        ResponseMessage<String> response = userService.login(username, password);
        if ("登录成功".equals(response.getData())) {
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
        String uploadDir = System.getProperty("user.dir") + "/uploads/avatars/";
        Path filePath = Paths.get(uploadDir + filename);

        try {
            byte[] imageBytes = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
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
