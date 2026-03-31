package org.example.novelplatformv.service;

import org.example.novelplatformv.entity.Comment;
import org.example.novelplatformv.util.ResponseMessage;

import java.util.List;

public interface CommentService {

    ResponseMessage<Comment> getCommentById(Long commentId);

    ResponseMessage<List<Comment>> getCommentsByNovelId(Long novelId, Integer page, Integer size);

    ResponseMessage<List<Comment>> getCommentsByChapterId(Long chapterId, Integer page, Integer size);

    ResponseMessage<List<Comment>> getCommentsByUserId(Long userId, Integer page, Integer size);

    ResponseMessage<List<Comment>> getAllComments(Integer page, Integer size);

    ResponseMessage<List<Comment>> getCommentReplies(Long parentId);

    ResponseMessage<Integer> getCommentCountByNovelId(Long novelId);

    ResponseMessage<Integer> getCommentCountByUserId(Long userId);

    ResponseMessage<String> createComment(Comment comment);

    ResponseMessage<String> updateComment(Comment comment);

    ResponseMessage<String> deleteComment(Long commentId);

    ResponseMessage<String> deleteBatchComments(List<Long> commentIds);

    ResponseMessage<String> likeComment(Long commentId);

    ResponseMessage<String> unlikeComment(Long commentId);
}
