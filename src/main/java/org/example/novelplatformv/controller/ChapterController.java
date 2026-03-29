package org.example.novelplatformv.controller;

import org.example.novelplatformv.entity.Chapter;
import org.example.novelplatformv.service.ChapterService;
import org.example.novelplatformv.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapter")
@CrossOrigin(origins = "*")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @GetMapping("/{chapterId}")
    public ResponseEntity<ResponseMessage<Chapter>> getChapterById(@PathVariable Long chapterId) {
        ResponseMessage<Chapter> response = chapterService.getChapterById(chapterId);
        if (response.getData() == null) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/novel/{novelId}")
    public ResponseEntity<ResponseMessage<List<Chapter>>> getChaptersByNovelId(@PathVariable Long novelId) {
        ResponseMessage<List<Chapter>> response = chapterService.getChaptersByNovelId(novelId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/novel/{novelId}/num/{chapterNum}")
    public ResponseEntity<ResponseMessage<Chapter>> getChapterByNovelIdAndNum(
            @PathVariable Long novelId,
            @PathVariable Integer chapterNum) {
        ResponseMessage<Chapter> response = chapterService.getChapterByNovelIdAndNum(novelId, chapterNum);
        if (response.getData() == null) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/novel/{novelId}/latest")
    public ResponseEntity<ResponseMessage<Chapter>> getLatestChapter(@PathVariable Long novelId) {
        ResponseMessage<Chapter> response = chapterService.getLatestChapter(novelId);
        if (response.getData() == null) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/novel/{novelId}/count")
    public ResponseEntity<ResponseMessage<Integer>> getChapterCount(@PathVariable Long novelId) {
        ResponseMessage<Integer> response = chapterService.getChapterCount(novelId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage<String>> createChapter(@RequestBody Chapter chapter) {
        ResponseMessage<String> response = chapterService.createChapter(chapter);
        if ("章节创建成功".equals(response.getData())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage<String>> updateChapter(@RequestBody Chapter chapter) {
        ResponseMessage<String> response = chapterService.updateChapter(chapter);
        if ("更新成功".equals(response.getData())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/delete/{chapterId}")
    public ResponseEntity<ResponseMessage<String>> deleteChapter(@PathVariable Long chapterId) {
        ResponseMessage<String> response = chapterService.deleteChapter(chapterId);
        if ("删除成功".equals(response.getData())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/delete/batch")
    public ResponseEntity<ResponseMessage<String>> deleteBatchChapters(@RequestBody List<Long> chapterIds) {
        ResponseMessage<String> response = chapterService.deleteBatchChapters(chapterIds);
        if ("批量删除成功".equals(response.getData())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
