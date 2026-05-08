package org.example.novelplatform.controller;

import org.example.novelplatform.entity.Novel;
import org.example.novelplatform.service.FileService;
import org.example.novelplatform.service.NovelService;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/novel")
public class NovelController {

    @Autowired
    private NovelService novelService;

    @Autowired
    private FileService fileService;

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
        return ResponseEntity.ok(novelService.getAllNovels());
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<ResponseMessage<List<Novel>>> getNovelsByAuthorId(@PathVariable Long authorId) {
        return ResponseEntity.ok(novelService.getNovelsByAuthorId(authorId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseMessage<List<Novel>>> getNovelsByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(novelService.getNovelsByCategoryId(categoryId));
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
        return ResponseEntity.ok(novelService.getHotNovels(limit));
    }

    @GetMapping("/latest")
    public ResponseEntity<ResponseMessage<List<Novel>>> getLatestNovels(
            @RequestParam(defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(novelService.getLatestNovels(limit));
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseMessage<List<Novel>>> searchNovels(
            @RequestParam String novelName) {
        return ResponseEntity.ok(novelService.searchNovels(novelName));
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
        return fileService.getCover(filename);
    }

    @GetMapping("/cover/base64/{filename}")
    public ResponseEntity<Map<String, String>> getCoverBase64(@PathVariable String filename) {
        return fileService.getCoverBase64(filename);
    }
}
