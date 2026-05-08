package org.example.novelplatform.controller;

import org.example.novelplatform.entity.Bookshelf;
import org.example.novelplatform.service.BookshelfService;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookshelf")
public class BookshelfController {

    private final BookshelfService bookshelfService;

    public BookshelfController(BookshelfService bookshelfService) {
        this.bookshelfService = bookshelfService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<Bookshelf>> getBookshelfById(@PathVariable Long id) {
        Bookshelf bookshelf = bookshelfService.getBookshelfById(id);
        return ResponseEntity.ok(ResponseMessage.success(bookshelf));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseMessage<List<Bookshelf>>> getBookshelfByUserId(@PathVariable Long userId) {
        List<Bookshelf> bookshelves = bookshelfService.getBookshelfByUserId(userId);
        return ResponseEntity.ok(ResponseMessage.success(bookshelves));
    }

    @GetMapping("/user/{userId}/novel/{novelId}")
    public ResponseEntity<ResponseMessage<Bookshelf>> getBookshelfByUserIdAndNovelId(
            @PathVariable Long userId,
            @PathVariable Long novelId) {
        Bookshelf bookshelf = bookshelfService.getBookshelfByUserIdAndNovelId(userId, novelId);
        return ResponseEntity.ok(ResponseMessage.success(bookshelf));
    }

    @GetMapping("/check")
    public ResponseEntity<ResponseMessage<Boolean>> checkIfAdded(
            @RequestParam Long userId,
            @RequestParam Long novelId) {
        boolean isAdded = bookshelfService.checkIfAdded(userId, novelId);
        return ResponseEntity.ok(ResponseMessage.success(isAdded));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ResponseMessage<Integer>> getBookshelfCount(@PathVariable Long userId) {
        int count = bookshelfService.getBookshelfCount(userId);
        return ResponseEntity.ok(ResponseMessage.success(count));
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseMessage<Bookshelf>> addToBookshelf(@RequestBody Map<String, Long> requestData) {
        Long userId = requestData.get("userId");
        Long novelId = requestData.get("novelId");

        Bookshelf bookshelf = bookshelfService.addToBookshelf(userId, novelId);
        return ResponseEntity.ok(ResponseMessage.success("加入书架成功", bookshelf));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ResponseMessage<Void>> removeFromBookshelf(@RequestBody Map<String, Long> requestData) {
        Long userId = requestData.get("userId");
        Long novelId = requestData.get("novelId");

        bookshelfService.removeFromBookshelf(userId, novelId);
        return ResponseEntity.ok(ResponseMessage.success("移出书架成功", null));
    }

    @DeleteMapping("/remove/batch")
    public ResponseEntity<ResponseMessage<Void>> removeFromBookshelfBatch(@RequestBody Map<String, Object> requestData) {
        Long userId = ((Number) requestData.get("userId")).longValue();
        @SuppressWarnings("unchecked")
        List<Long> novelIds = (List<Long>) requestData.get("novelIds");

        bookshelfService.removeFromBookshelfBatch(userId, novelIds);
        return ResponseEntity.ok(ResponseMessage.success("批量移出书架成功", null));
    }

    @PutMapping("/reading-progress")
    public ResponseEntity<ResponseMessage<Bookshelf>> updateReadingProgress(@RequestBody Map<String, Long> requestData) {
        Long userId = requestData.get("userId");
        Long novelId = requestData.get("novelId");
        Long chapterId = requestData.get("chapterId");

        Bookshelf bookshelf = bookshelfService.updateReadingProgress(userId, novelId, chapterId);
        return ResponseEntity.ok(ResponseMessage.success("阅读进度已更新", bookshelf));
    }
}
