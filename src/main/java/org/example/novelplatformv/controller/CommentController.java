package org.example.novelplatformv.controller;

import org.example.novelplatformv.entity.Comment;
import org.example.novelplatformv.service.CommentService;
import org.example.novelplatformv.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{commentId}")
    public ResponseEntity<ResponseMessage<Comment>> getCommentById(@PathVariable Long commentId) {
        ResponseMessage<Comment> response = commentService.getCommentById(commentId);
        if (response.getData() == null) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/novel/{novelId}")
    public ResponseEntity<ResponseMessage<List<Comment>>> getCommentsByNovelId(
            @PathVariable Long novelId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        ResponseMessage<List<Comment>> response = commentService.getCommentsByNovelId(novelId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<ResponseMessage<List<Comment>>> getCommentsByChapterId(
            @PathVariable Long chapterId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        ResponseMessage<List<Comment>> response = commentService.getCommentsByChapterId(chapterId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseMessage<List<Comment>>> getCommentsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        ResponseMessage<List<Comment>> response = commentService.getCommentsByUserId(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/replies/{parentId}")
    public ResponseEntity<ResponseMessage<List<Comment>>> getCommentReplies(@PathVariable Long parentId) {
        ResponseMessage<List<Comment>> response = commentService.getCommentReplies(parentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/novel/{novelId}/count")
    public ResponseEntity<ResponseMessage<Integer>> getCommentCountByNovelId(@PathVariable Long novelId) {
        ResponseMessage<Integer> response = commentService.getCommentCountByNovelId(novelId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ResponseMessage<Integer>> getCommentCountByUserId(@PathVariable Long userId) {
        ResponseMessage<Integer> response = commentService.getCommentCountByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage<String>> createComment(@RequestBody Comment comment) {
        ResponseMessage<String> response = commentService.createComment(comment);
        if ("评论成功".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage<String>> updateComment(@RequestBody Comment comment) {
        ResponseMessage<String> response = commentService.updateComment(comment);
        if ("更新成功".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<ResponseMessage<String>> deleteComment(@PathVariable Long commentId) {
        ResponseMessage<String> response = commentService.deleteComment(commentId);
        if ("删除成功".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/delete/batch")
    public ResponseEntity<ResponseMessage<String>> deleteBatchComments(@RequestBody List<Long> commentIds) {
        ResponseMessage<String> response = commentService.deleteBatchComments(commentIds);
        if ("批量删除成功".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<ResponseMessage<String>> likeComment(@PathVariable Long commentId) {
        ResponseMessage<String> response = commentService.likeComment(commentId);
        if ("点赞成功".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{commentId}/unlike")
    public ResponseEntity<ResponseMessage<String>> unlikeComment(@PathVariable Long commentId) {
        ResponseMessage<String> response = commentService.unlikeComment(commentId);
        if ("取消点赞成功".equals(response.getMessage())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
