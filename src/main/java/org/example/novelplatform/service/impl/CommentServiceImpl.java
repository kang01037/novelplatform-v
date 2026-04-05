package org.example.novelplatform.service.impl;

import org.example.novelplatform.entity.Comment;
import org.example.novelplatform.entity.Novel;
import org.example.novelplatform.mapper.CommentMapper;
import org.example.novelplatform.mapper.NovelMapper;
import org.example.novelplatform.service.CommentService;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private NovelMapper novelMapper;

    @Override
    public ResponseMessage<Comment> getCommentById(Long commentId) {
        Comment comment = commentMapper.selectByCommentId(commentId);
        if (comment == null) {
            return ResponseMessage.error("评论不存在");
        }
        return ResponseMessage.success(comment);
    }

    @Override
    public ResponseMessage<List<Comment>> getCommentsByNovelId(Long novelId, Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 20;

        int offset = (page - 1) * size;
        List<Comment> comments = commentMapper.selectByNovelId(novelId, offset, size);
        return ResponseMessage.success(comments);
    }

    @Override
    public ResponseMessage<List<Comment>> getCommentsByChapterId(Long chapterId, Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 20;

        int offset = (page - 1) * size;
        List<Comment> comments = commentMapper.selectByChapterId(chapterId, offset, size);
        return ResponseMessage.success(comments);
    }

    @Override
    public ResponseMessage<List<Comment>> getCommentsByUserId(Long userId, Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 20;

        int offset = (page - 1) * size;
        List<Comment> comments = commentMapper.selectByUserId(userId, offset, size);
        return ResponseMessage.success(comments);
    }

    @Override
    public ResponseMessage<List<Comment>> getAllComments(Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 20;

        int offset = (page - 1) * size;
        List<Comment> comments = commentMapper.selectAllComments(offset, size);
        return ResponseMessage.success(comments);
    }

    @Override
    public ResponseMessage<List<Comment>> getCommentReplies(Long parentId) {
        List<Comment> replies = commentMapper.selectReplies(parentId);
        return ResponseMessage.success(replies);
    }

    @Override
    public ResponseMessage<Integer> getCommentCountByNovelId(Long novelId) {
        int count = commentMapper.countByNovelId(novelId);
        return ResponseMessage.success(count);
    }

    @Override
    public ResponseMessage<Integer> getCommentCountByUserId(Long userId) {
        int count = commentMapper.countByUserId(userId);
        return ResponseMessage.success(count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage<String> createComment(Comment comment) {
        if (comment.getUserId() == null || comment.getNovelId() == null) {
            return ResponseMessage.error("用户 ID 和小说 ID 不能为空");
        }

        Novel novel = novelMapper.selectByNovelId(comment.getNovelId());
        if (novel == null) {
            return ResponseMessage.error("小说不存在");
        }

        comment.setCreatedTime(LocalDateTime.now());
        comment.setUpdatedTime(LocalDateTime.now());
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setStatus(1);
        comment.setDeleted(0);

        if (comment.getParentId() == null) {
            comment.setParentId(0L);
        }

        int result = commentMapper.insertComment(comment);
        if (result > 0) {
            if (comment.getParentId() != null && comment.getParentId() > 0) {
                commentMapper.incrementReplyCount(comment.getParentId());
            }
            return ResponseMessage.success("评论成功", "评论成功");
        } else {
            return ResponseMessage.error("评论失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage<String> updateComment(Comment comment) {
        Comment existingComment = commentMapper.selectByCommentId(comment.getCommentId());
        if (existingComment == null) {
            return ResponseMessage.error("评论不存在");
        }

        comment.setUpdatedTime(LocalDateTime.now());
        int result = commentMapper.updateComment(comment);
        if (result > 0) {
            return ResponseMessage.success("更新成功", "更新成功");
        } else {
            return ResponseMessage.error("更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage<String> deleteComment(Long commentId) {
        Comment comment = commentMapper.selectByCommentId(commentId);
        if (comment == null) {
            return ResponseMessage.error("评论不存在");
        }

        int result = commentMapper.deleteComment(commentId);
        if (result > 0) {
            if (comment.getParentId() != null && comment.getParentId() > 0) {
                commentMapper.decrementReplyCount(comment.getParentId());
            }
            return ResponseMessage.success("删除成功", "删除成功");
        } else {
            return ResponseMessage.error("删除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage<String> deleteBatchComments(List<Long> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            return ResponseMessage.error("评论 ID 列表不能为空");
        }

        int result = commentMapper.deleteBatchComments(commentIds);
        if (result > 0) {
            return ResponseMessage.success("批量删除成功", "批量删除成功");
        } else {
            return ResponseMessage.error("批量删除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage<String> likeComment(Long commentId) {
        Comment comment = commentMapper.selectByCommentId(commentId);
        if (comment == null) {
            return ResponseMessage.error("评论不存在");
        }

        int result = commentMapper.incrementLikeCount(commentId);
        if (result > 0) {
            return ResponseMessage.success("点赞成功", "点赞成功");
        } else {
            return ResponseMessage.error("点赞失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage<String> unlikeComment(Long commentId) {
        Comment comment = commentMapper.selectByCommentId(commentId);
        if (comment == null) {
            return ResponseMessage.error("评论不存在");
        }

        int result = commentMapper.decrementLikeCount(commentId);
        if (result > 0) {
            return ResponseMessage.success("取消点赞成功", "取消点赞成功");
        } else {
            return ResponseMessage.error("取消点赞失败");
        }
    }
}
