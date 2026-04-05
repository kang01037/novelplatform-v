package org.example.novelplatform.controller;

import org.example.novelplatform.entity.Novel;
import org.example.novelplatform.service.NovelService;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/novel")
@CrossOrigin(origins = "*")
public class NovelController {

    @Autowired
    private NovelService novelService;

    @GetMapping("/{novelId}")
    public ResponseEntity<ResponseMessage<Novel>> getNovelById(@PathVariable Long novelId) {
        ResponseMessage<Novel> response = novelService.getNovelById(novelId);
        if (response.getData() == null) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseMessage<List<Novel>>> getAllNovels() {
        ResponseMessage<List<Novel>> response = novelService.getAllNovels();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<ResponseMessage<List<Novel>>> getNovelsByAuthorId(@PathVariable Long authorId) {
        ResponseMessage<List<Novel>> response = novelService.getNovelsByAuthorId(authorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseMessage<List<Novel>>> getNovelsByCategoryId(@PathVariable Long categoryId) {
        ResponseMessage<List<Novel>> response = novelService.getNovelsByCategoryId(categoryId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage<String>> createNovel(@RequestBody Novel novel) {
        ResponseMessage<String> response = novelService.createNovel(novel);
        if ("小说创建成功".equals(response.getData())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage<String>> updateNovel(@RequestBody Novel novel) {
        ResponseMessage<String> response = novelService.updateNovel(novel);
        if ("更新成功".equals(response.getData())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/delete/{novelId}")
    public ResponseEntity<ResponseMessage<String>> deleteNovel(@PathVariable Long novelId) {
        ResponseMessage<String> response = novelService.deleteNovel(novelId);
        if ("删除成功".equals(response.getData())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/{novelId}/click")
    public ResponseEntity<ResponseMessage<String>> incrementClickCount(@PathVariable Long novelId) {
        ResponseMessage<String> response = novelService.incrementClickCount(novelId);
        if ("点击量已更新".equals(response.getData())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{novelId}/collect")
    public ResponseEntity<ResponseMessage<String>> incrementCollectCount(@PathVariable Long novelId) {
        ResponseMessage<String> response = novelService.incrementCollectCount(novelId);
        if ("收藏量已更新".equals(response.getData())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{novelId}/recommend")
    public ResponseEntity<ResponseMessage<String>> incrementRecommendCount(@PathVariable Long novelId) {
        ResponseMessage<String> response = novelService.incrementRecommendCount(novelId);
        if ("推荐量已更新".equals(response.getData())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{novelId}/rate")
    public ResponseEntity<ResponseMessage<String>> rateNovel(
            @PathVariable Long novelId,
            @RequestBody Map<String, BigDecimal> requestData) {
        BigDecimal score = requestData.get("score");
        ResponseMessage<String> response = novelService.rateNovel(novelId, score);
        if ("评分成功".equals(response.getData())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/hot")
    public ResponseEntity<ResponseMessage<List<Novel>>> getHotNovels(
            @RequestParam(defaultValue = "10") Integer limit) {
        ResponseMessage<List<Novel>> response = novelService.getHotNovels(limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<ResponseMessage<List<Novel>>> getLatestNovels(
            @RequestParam(defaultValue = "10") Integer limit) {
        ResponseMessage<List<Novel>> response = novelService.getLatestNovels(limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseMessage<List<Novel>>> searchNovels(
            @RequestParam String novelName) {
        ResponseMessage<List<Novel>> response = novelService.searchNovels(novelName);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload/cover")
    public ResponseEntity<ResponseMessage<String>> uploadCover(
            @RequestParam("novelId") Long novelId,
            @RequestParam("file") MultipartFile file) {
        ResponseMessage<String> response = novelService.uploadCover(novelId, file);
        if ("封面上传成功".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/cover/{filename}")
    public ResponseEntity<byte[]> getCover(@PathVariable String filename) {
        String uploadDir = System.getProperty("user.dir") + "/uploads/covers/";
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
}
