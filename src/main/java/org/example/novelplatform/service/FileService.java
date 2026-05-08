package org.example.novelplatform.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {

    ResponseEntity<byte[]> getAvatar(String filename);

    ResponseEntity<byte[]> getCover(String filename);

    ResponseEntity<Map<String, String>> getCoverBase64(String filename);
}
