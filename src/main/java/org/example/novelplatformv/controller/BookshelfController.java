package org.example.novelplatformv.controller;

import org.example.novelplatformv.entity.Bookshelf;
import org.example.novelplatformv.service.BookshelfService;
import org.example.novelplatformv.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookshelf")
@CrossOrigin(origins = "*")
public class BookshelfController {

    @Autowired
    private BookshelfService bookshelfService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<Bookshelf>> getBookshelfById(@PathVariable Long id) {
        ResponseMessage<Bookshelf> response = bookshelfService.getBookshelfById(id);
        if (response.getData() == null) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseMessage<List<Bookshelf>>> getBookshelfByUserId(@PathVariable Long userId) {
        ResponseMessage<List<Bookshelf>> response = bookshelfService.getBookshelfByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/novel/{novelId}")
    public ResponseEntity<ResponseMessage<Bookshelf>> getBookshelfByUserIdAndNovelId(
            @PathVariable Long userId,
            @PathVariable Long novelId) {
        ResponseMessage<Bookshelf> response = bookshelfService.getBookshelfByUserIdAndNovelId(userId, novelId);
        if (response.getData() == null) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<ResponseMessage<Boolean>> checkIfAdded(
            @RequestParam Long userId,
            @RequestParam Long novelId) {
        ResponseMessage<Boolean> response = bookshelfService.checkIfAdded(userId, novelId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ResponseMessage<Integer>> getBookshelfCount(@PathVariable Long userId) {
        ResponseMessage<Integer> response = bookshelfService.getBookshelfCount(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseMessage<String>> addToBookshelf(@RequestBody Map<String, Long> requestData) {
        Long userId = requestData.get("userId");
        Long novelId = requestData.get("novelId");

        ResponseMessage<String> response = bookshelfService.addToBookshelf(userId, novelId);
        if ("加入书架成功".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ResponseMessage<String>> removeFromBookshelf(@RequestBody Map<String, Long> requestData) {
        Long userId = requestData.get("userId");
        Long novelId = requestData.get("novelId");

        ResponseMessage<String> response = bookshelfService.removeFromBookshelf(userId, novelId);
        if ("移出书架成功".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/remove/batch")
    public ResponseEntity<ResponseMessage<String>> removeFromBookshelfBatch(@RequestBody Map<String, Object> requestData) {
        Long userId = ((Number) requestData.get("userId")).longValue();
        @SuppressWarnings("unchecked")
        List<Long> novelIds = (List<Long>) requestData.get("novelIds");

        ResponseMessage<String> response = bookshelfService.removeFromBookshelfBatch(userId, novelIds);
        if ("批量移出书架成功".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/reading-progress")
    public ResponseEntity<ResponseMessage<String>> updateReadingProgress(@RequestBody Map<String, Long> requestData) {
        Long userId = requestData.get("userId");
        Long novelId = requestData.get("novelId");
        Long chapterId = requestData.get("chapterId");

        ResponseMessage<String> response = bookshelfService.updateReadingProgress(userId, novelId, chapterId);
        if ("阅读进度已更新".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
