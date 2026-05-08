package org.example.novelplatform.service.impl;

import org.example.novelplatform.entity.Comment;
import org.example.novelplatform.entity.Novel;
import org.example.novelplatform.exception.ServiceException;
import org.example.novelplatform.mapper.CommentMapper;
import org.example.novelplatform.mapper.NovelMapper;
import org.example.novelplatform.service.CommentService;
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
    public Comment getCommentById(Long commentId) {
        Comment comment = commentMapper.selectByCommentId(commentId);
        if (comment == null) {
            throw new ServiceException("评论不存在");
        }
        return comment;
    }

    @Override
    public List<Comment> getCommentsByNovelId(Long novelId, Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 20;

        int offset = (page - 1) * size;
        return commentMapper.selectByNovelId(novelId, offset, size);
    }

    @Override
    public List<Comment> getCommentsByChapterId(Long chapterId, Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 20;

        int offset = (page - 1) * size;
        return commentMapper.selectByChapterId(chapterId, offset, size);
    }

    @Override
    public List<Comment> getCommentsByUserId(Long userId, Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 20;

        int offset = (page - 1) * size;
        return commentMapper.selectByUserId(userId, offset, size);
    }

    @Override
    public List<Comment> getAllComments(Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 20;

        int offset = (page - 1) * size;
        return commentMapper.selectAllComments(offset, size);
    }

    @Override
    public List<Comment> getCommentReplies(Long parentId) {
        return commentMapper.selectReplies(parentId);
    }

    @Override
    public int getCommentCountByNovelId(Long novelId) {
        return commentMapper.countByNovelId(novelId);
    }

    @Override
    public int getCommentCountByUserId(Long userId) {
        return commentMapper.countByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Comment createComment(Comment comment) {
        if (comment.getUserId() == null || comment.getNovelId() == null) {
            throw new ServiceException("用户 ID 和小说 ID 不能为空");
        }

        Novel novel = novelMapper.selectByNovelId(comment.getNovelId());
        if (novel == null) {
            throw new ServiceException("小说不存在");
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
            return commentMapper.selectByCommentId(comment.getCommentId());
        } else {
            throw new ServiceException("评论失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Comment updateComment(Comment comment) {
        Comment existingComment = commentMapper.selectByCommentId(comment.getCommentId());
        if (existingComment == null) {
            throw new ServiceException("评论不存在");
        }

        comment.setUpdatedTime(LocalDateTime.now());
        int result = commentMapper.updateComment(comment);
        if (result > 0) {
            return commentMapper.selectByCommentId(comment.getCommentId());
        } else {
            throw new ServiceException("更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId) {
        Comment comment = commentMapper.selectByCommentId(commentId);
        if (comment == null) {
            throw new ServiceException("评论不存在");
        }

        int result = commentMapper.deleteComment(commentId);
        if (result > 0) {
            if (comment.getParentId() != null && comment.getParentId() > 0) {
                commentMapper.decrementReplyCount(comment.getParentId());
            }
        } else {
            throw new ServiceException("删除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchComments(List<Long> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            throw new ServiceException("评论 ID 列表不能为空");
        }

        int result = commentMapper.deleteBatchComments(commentIds);
        if (result <= 0) {
            throw new ServiceException("批量删除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeComment(Long commentId) {
        Comment comment = commentMapper.selectByCommentId(commentId);
        if (comment == null) {
            throw new ServiceException("评论不存在");
        }

        int result = commentMapper.incrementLikeCount(commentId);
        if (result <= 0) {
            throw new ServiceException("点赞失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikeComment(Long commentId) {
        Comment comment = commentMapper.selectByCommentId(commentId);
        if (comment == null) {
            throw new ServiceException("评论不存在");
        }

        int result = commentMapper.decrementLikeCount(commentId);
        if (result <= 0) {
            throw new ServiceException("取消点赞失败");
        }
    }
}
