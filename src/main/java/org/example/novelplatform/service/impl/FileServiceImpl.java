package org.example.novelplatform.service.impl;

import org.example.novelplatform.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {

    private static final String AVATAR_DIR = "/uploads/avatars/";
    private static final String COVER_DIR = "/uploads/covers/";

    @Override
    public ResponseEntity<byte[]> getAvatar(String filename) {
        return getFile(AVATAR_DIR, filename);
    }

    @Override
    public ResponseEntity<byte[]> getCover(String filename) {
        return getFile(COVER_DIR, filename);
    }

    @Override
    public ResponseEntity<Map<String, String>> getCoverBase64(String filename) {
        return getFileBase64(COVER_DIR, filename);
    }

    private ResponseEntity<byte[]> getFile(String uploadDir, String filename) {
        String fullPath = System.getProperty("user.dir") + uploadDir + filename;
        Path filePath = Paths.get(fullPath);

        try {
            byte[] imageBytes = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<Map<String, String>> getFileBase64(String uploadDir, String filename) {
        String fullPath = System.getProperty("user.dir") + uploadDir + filename;
        Path filePath = Paths.get(fullPath);

        Map<String, String> result = new HashMap<>();

        try {
            byte[] imageBytes = Files.readAllBytes(filePath);
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String base64Data = "data:image/jpeg;base64," + base64Image;

            result.put("image", base64Data);
            result.put("filename", filename);

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            result.put("error", "图片不存在");
            return ResponseEntity.status(404).body(result);
        }
    }
}
