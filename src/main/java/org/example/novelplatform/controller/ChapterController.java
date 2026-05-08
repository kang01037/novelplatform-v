package org.example.novelplatform.controller;

import org.example.novelplatform.entity.Chapter;
import org.example.novelplatform.service.ChapterService;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapter")
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping("/{chapterId}")
    public ResponseEntity<ResponseMessage<Chapter>> getChapterById(@PathVariable Long chapterId) {
        Chapter chapter = chapterService.getChapterById(chapterId);
        return ResponseEntity.ok(ResponseMessage.success(chapter));
    }

    @GetMapping("/novel/{novelId}")
    public ResponseEntity<ResponseMessage<List<Chapter>>> getChaptersByNovelId(@PathVariable Long novelId) {
        List<Chapter> chapters = chapterService.getChaptersByNovelId(novelId);
        return ResponseEntity.ok(ResponseMessage.success(chapters));
    }

    @GetMapping("/novel/{novelId}/num/{chapterNum}")
    public ResponseEntity<ResponseMessage<Chapter>> getChapterByNovelIdAndNum(
            @PathVariable Long novelId,
            @PathVariable Integer chapterNum) {
        Chapter chapter = chapterService.getChapterByNovelIdAndNum(novelId, chapterNum);
        return ResponseEntity.ok(ResponseMessage.success(chapter));
    }

    @GetMapping("/novel/{novelId}/latest")
    public ResponseEntity<ResponseMessage<Chapter>> getLatestChapter(@PathVariable Long novelId) {
        Chapter chapter = chapterService.getLatestChapter(novelId);
        return ResponseEntity.ok(ResponseMessage.success(chapter));
    }

    @GetMapping("/novel/{novelId}/count")
    public ResponseEntity<ResponseMessage<Integer>> getChapterCount(@PathVariable Long novelId) {
        int count = chapterService.getChapterCount(novelId);
        return ResponseEntity.ok(ResponseMessage.success(count));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage<Chapter>> createChapter(@RequestBody Chapter chapter) {
        Chapter createdChapter = chapterService.createChapter(chapter);
        return ResponseEntity.ok(ResponseMessage.success("章节创建成功", createdChapter));
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage<Chapter>> updateChapter(@RequestBody Chapter chapter) {
        Chapter updatedChapter = chapterService.updateChapter(chapter);
        return ResponseEntity.ok(ResponseMessage.success("更新成功", updatedChapter));
    }

    @DeleteMapping("/delete/{chapterId}")
    public ResponseEntity<ResponseMessage<Void>> deleteChapter(@PathVariable Long chapterId) {
        chapterService.deleteChapter(chapterId);
        return ResponseEntity.ok(ResponseMessage.success("删除成功", null));
    }

    @DeleteMapping("/delete/batch")
    public ResponseEntity<ResponseMessage<Void>> deleteBatchChapters(@RequestBody List<Long> chapterIds) {
        chapterService.deleteBatchChapters(chapterIds);
        return ResponseEntity.ok(ResponseMessage.success("批量删除成功", null));
    }
}
