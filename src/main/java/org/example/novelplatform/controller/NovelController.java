package org.example.novelplatform.controller;

import org.example.novelplatform.entity.Novel;
import org.example.novelplatform.service.FileService;
import org.example.novelplatform.service.NovelService;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/novel")
public class NovelController {

    private final NovelService novelService;
    private final FileService fileService;

    public NovelController(NovelService novelService, FileService fileService) {
        this.novelService = novelService;
        this.fileService = fileService;
    }

    @GetMapping("/{novelId}")
    public ResponseEntity<ResponseMessage<Novel>> getNovelById(@PathVariable Long novelId) {
        Novel novel = novelService.getNovelById(novelId);
        return ResponseEntity.ok(ResponseMessage.success(novel));
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseMessage<List<Novel>>> getAllNovels() {
        List<Novel> novels = novelService.getAllNovels();
        return ResponseEntity.ok(ResponseMessage.success(novels));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<ResponseMessage<List<Novel>>> getNovelsByAuthorId(@PathVariable Long authorId) {
        List<Novel> novels = novelService.getNovelsByAuthorId(authorId);
        return ResponseEntity.ok(ResponseMessage.success(novels));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseMessage<List<Novel>>> getNovelsByCategoryId(@PathVariable Long categoryId) {
        List<Novel> novels = novelService.getNovelsByCategoryId(categoryId);
        return ResponseEntity.ok(ResponseMessage.success(novels));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage<Novel>> createNovel(@RequestBody Novel novel) {
        Novel createdNovel = novelService.createNovel(novel);
        return ResponseEntity.ok(ResponseMessage.success("小说创建成功", createdNovel));
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage<Novel>> updateNovel(@RequestBody Novel novel) {
        Novel updatedNovel = novelService.updateNovel(novel);
        return ResponseEntity.ok(ResponseMessage.success("更新成功", updatedNovel));
    }

    @DeleteMapping("/delete/{novelId}")
    public ResponseEntity<ResponseMessage<Void>> deleteNovel(@PathVariable Long novelId) {
        novelService.deleteNovel(novelId);
        return ResponseEntity.ok(ResponseMessage.success("删除成功", null));
    }

    @PostMapping("/{novelId}/click")
    public ResponseEntity<ResponseMessage<Void>> incrementClickCount(@PathVariable Long novelId) {
        novelService.incrementClickCount(novelId);
        return ResponseEntity.ok(ResponseMessage.success("点击量已更新", null));
    }

    @PostMapping("/{novelId}/collect")
    public ResponseEntity<ResponseMessage<Void>> incrementCollectCount(@PathVariable Long novelId) {
        novelService.incrementCollectCount(novelId);
        return ResponseEntity.ok(ResponseMessage.success("收藏量已更新", null));
    }

    @PostMapping("/{novelId}/recommend")
    public ResponseEntity<ResponseMessage<Void>> incrementRecommendCount(@PathVariable Long novelId) {
        novelService.incrementRecommendCount(novelId);
        return ResponseEntity.ok(ResponseMessage.success("推荐量已更新", null));
    }

    @PostMapping("/{novelId}/rate")
    public ResponseEntity<ResponseMessage<Void>> rateNovel(
            @PathVariable Long novelId,
            @RequestBody Map<String, BigDecimal> requestData) {
        BigDecimal score = requestData.get("score");
        novelService.rateNovel(novelId, score);
        return ResponseEntity.ok(ResponseMessage.success("评分成功", null));
    }

    @GetMapping("/hot")
    public ResponseEntity<ResponseMessage<List<Novel>>> getHotNovels(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Novel> novels = novelService.getHotNovels(limit);
        return ResponseEntity.ok(ResponseMessage.success(novels));
    }

    @GetMapping("/latest")
    public ResponseEntity<ResponseMessage<List<Novel>>> getLatestNovels(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Novel> novels = novelService.getLatestNovels(limit);
        return ResponseEntity.ok(ResponseMessage.success(novels));
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseMessage<List<Novel>>> searchNovels(
            @RequestParam String novelName) {
        List<Novel> novels = novelService.searchNovels(novelName);
        return ResponseEntity.ok(ResponseMessage.success(novels));
    }

    @PostMapping("/upload/cover")
    public ResponseEntity<ResponseMessage<String>> uploadCover(
            @RequestParam("novelId") Long novelId,
            @RequestParam("file") MultipartFile file) {
        String coverUrl = novelService.uploadCover(novelId, file);
        return ResponseEntity.ok(ResponseMessage.success("封面上传成功", coverUrl));
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
