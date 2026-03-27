package org.example.novelplatformv.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.novelplatformv.entity.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 根据评论 ID 查询评论
     * @param commentId 评论 ID
     * @return 评论对象，如果不存在则返回 null
     */
    Comment selectByCommentId(@Param("commentId") Long commentId);

    /**
     * 根据小说 ID 查询评论列表（分页查询）
     * @param novelId 小说 ID
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 评论列表
     */
    List<Comment> selectByNovelId(
            @Param("novelId") Long novelId,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);

    /**
     * 根据章节 ID 查询评论列表
     * @param chapterId 章节 ID
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 评论列表
     */
    List<Comment> selectByChapterId(
            @Param("chapterId") Long chapterId,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);

    /**
     * 根据用户 ID 查询评论列表
     * @param userId 用户 ID
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 评论列表
     */
    List<Comment> selectByUserId(
            @Param("userId") Long userId,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);

    /**
     * 查询某条评论的子评论（回复）
     * @param parentId 父评论 ID
     * @return 子评论列表
     */
    List<Comment> selectReplies(@Param("parentId") Long parentId);

    /**
     * 统计小说评论数量
     * @param novelId 小说 ID
     * @return 评论数量
     */
    int countByNovelId(@Param("novelId") Long novelId);

    /**
     * 统计用户评论数量
     * @param userId 用户 ID
     * @return 评论数量
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 插入新评论
     * @param comment 评论对象
     * @return 影响的行数，大于 0 表示插入成功
     */
    int insertComment(Comment comment);

    /**
     * 更新评论信息
     * @param comment 评论对象
     * @return 影响的行数，大于 0 表示更新成功
     */
    int updateComment(Comment comment);

    /**
     * 删除评论（软删除）
     * @param commentId 评论 ID
     * @return 影响的行数，大于 0 表示删除成功
     */
    int deleteComment(@Param("commentId") Long commentId);

    /**
     * 增加点赞数
     * @param commentId 评论 ID
     * @return 影响的行数
     */
    int incrementLikeCount(@Param("commentId") Long commentId);

    /**
     * 减少点赞数
     * @param commentId 评论 ID
     * @return 影响的行数
     */
    int decrementLikeCount(@Param("commentId") Long commentId);

    /**
     * 增加回复数
     * @param commentId 评论 ID
     * @return 影响的行数
     */
    int incrementReplyCount(@Param("commentId") Long commentId);

    /**
     * 减少回复数
     * @param commentId 评论 ID
     * @return 影响的行数
     */
    int decrementReplyCount(@Param("commentId") Long commentId);

    /**
     * 批量删除评论
     * @param commentIds 评论 ID 列表
     * @return 影响的行数
     */
    int deleteBatchComments(@Param("commentIds") List<Long> commentIds);
}
