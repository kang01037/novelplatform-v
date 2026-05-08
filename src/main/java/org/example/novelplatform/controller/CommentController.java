package org.example.novelplatform.controller;

import org.example.novelplatform.entity.Comment;
import org.example.novelplatform.service.CommentService;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ResponseMessage<Comment>> getCommentById(@PathVariable Long commentId) {
        Comment comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(ResponseMessage.success(comment));
    }

    @GetMapping("/novel/{novelId}")
    public ResponseEntity<ResponseMessage<List<Comment>>> getCommentsByNovelId(
            @PathVariable Long novelId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<Comment> comments = commentService.getCommentsByNovelId(novelId, page, size);
        return ResponseEntity.ok(ResponseMessage.success(comments));
    }

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<ResponseMessage<List<Comment>>> getCommentsByChapterId(
            @PathVariable Long chapterId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<Comment> comments = commentService.getCommentsByChapterId(chapterId, page, size);
        return ResponseEntity.ok(ResponseMessage.success(comments));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseMessage<List<Comment>>> getCommentsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<Comment> comments = commentService.getCommentsByUserId(userId, page, size);
        return ResponseEntity.ok(ResponseMessage.success(comments));
    }

    @GetMapping("/replies/{parentId}")
    public ResponseEntity<ResponseMessage<List<Comment>>> getCommentReplies(@PathVariable Long parentId) {
        List<Comment> replies = commentService.getCommentReplies(parentId);
        return ResponseEntity.ok(ResponseMessage.success(replies));
    }

    @GetMapping("/novel/{novelId}/count")
    public ResponseEntity<ResponseMessage<Integer>> getCommentCountByNovelId(@PathVariable Long novelId) {
        int count = commentService.getCommentCountByNovelId(novelId);
        return ResponseEntity.ok(ResponseMessage.success(count));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ResponseMessage<Integer>> getCommentCountByUserId(@PathVariable Long userId) {
        int count = commentService.getCommentCountByUserId(userId);
        return ResponseEntity.ok(ResponseMessage.success(count));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage<Comment>> createComment(@RequestBody Comment comment) {
        Comment createdComment = commentService.createComment(comment);
        return ResponseEntity.ok(ResponseMessage.success("评论成功", createdComment));
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage<Comment>> updateComment(@RequestBody Comment comment) {
        Comment updatedComment = commentService.updateComment(comment);
        return ResponseEntity.ok(ResponseMessage.success("更新成功", updatedComment));
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<ResponseMessage<Void>> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ResponseMessage.success("删除成功", null));
    }

    @DeleteMapping("/delete/batch")
    public ResponseEntity<ResponseMessage<Void>> deleteBatchComments(@RequestBody List<Long> commentIds) {
        commentService.deleteBatchComments(commentIds);
        return ResponseEntity.ok(ResponseMessage.success("批量删除成功", null));
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<ResponseMessage<Void>> likeComment(@PathVariable Long commentId) {
        commentService.likeComment(commentId);
        return ResponseEntity.ok(ResponseMessage.success("点赞成功", null));
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<Comment>>> getAllComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<Comment> comments = commentService.getAllComments(page, size);
        return ResponseEntity.ok(ResponseMessage.success(comments));
    }

    @PostMapping("/{commentId}/unlike")
    public ResponseEntity<ResponseMessage<Void>> unlikeComment(@PathVariable Long commentId) {
        commentService.unlikeComment(commentId);
        return ResponseEntity.ok(ResponseMessage.success("取消点赞成功", null));
    }
}
