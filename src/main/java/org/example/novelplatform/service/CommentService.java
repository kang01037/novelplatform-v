package org.example.novelplatform.service;

import org.example.novelplatform.entity.Comment;

import java.util.List;

public interface CommentService {

    Comment getCommentById(Long commentId);

    List<Comment> getCommentsByNovelId(Long novelId, Integer page, Integer size);

    List<Comment> getCommentsByChapterId(Long chapterId, Integer page, Integer size);

    List<Comment> getCommentsByUserId(Long userId, Integer page, Integer size);

    List<Comment> getAllComments(Integer page, Integer size);

    List<Comment> getCommentReplies(Long parentId);

    int getCommentCountByNovelId(Long novelId);

    int getCommentCountByUserId(Long userId);

    Comment createComment(Comment comment);

    Comment updateComment(Comment comment);

    void deleteComment(Long commentId);

    void deleteBatchComments(List<Long> commentIds);

    void likeComment(Long commentId);

    void unlikeComment(Long commentId);
}
