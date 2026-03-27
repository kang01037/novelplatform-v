package org.example.novelplatformv.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.novelplatformv.entity.Bookshelf;

import java.util.List;

@Mapper
public interface BookshelfMapper {

    /**
     * 根据 ID 查询书架记录
     * @param id 书架 ID
     * @return 书架对象，如果不存在则返回 null
     */
    Bookshelf selectById(@Param("id") Long id);

    /**
     * 根据用户 ID 查询书架列表（用户收藏的所有小说）
     * @param userId 用户 ID
     * @return 书架列表
     */
    List<Bookshelf> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户 ID 和小说 ID 查询书架记录
     * @param userId 用户 ID
     * @param novelId 小说 ID
     * @return 书架对象，如果不存在则返回 null
     */
    Bookshelf selectByUserIdAndNovelId(
            @Param("userId") Long userId,
            @Param("novelId") Long novelId);

    /**
     * 检查用户是否已收藏某小说
     * @param userId 用户 ID
     * @param novelId 小说 ID
     * @return true-已收藏，false-未收藏
     */
    boolean isAddedToBookshelf(
            @Param("userId") Long userId,
            @Param("novelId") Long novelId);

    /**
     * 添加到书架（收藏小说）
     * @param bookshelf 书架对象
     * @return 影响的行数，大于 0 表示添加成功
     */
    int insertBookshelf(Bookshelf bookshelf);

    /**
     * 更新书架信息（主要是更新阅读进度）
     * @param bookshelf 书架对象
     * @return 影响的行数，大于 0 表示更新成功
     */
    int updateBookshelf(Bookshelf bookshelf);

    /**
     * 从书架移除小说（取消收藏）
     * @param userId 用户 ID
     * @param novelId 小说 ID
     * @return 影响的行数，大于 0 表示删除成功
     */
    int deleteFromBookshelf(
            @Param("userId") Long userId,
            @Param("novelId") Long novelId);

    /**
     * 批量从书架移除小说
     * @param userId 用户 ID
     * @param novelIds 小说 ID 列表
     * @return 影响的行数
     */
    int deleteBatchFromBookshelf(
            @Param("userId") Long userId,
            @Param("novelIds") List<Long> novelIds);

    /**
     * 统计用户收藏数量
     * @param userId 用户 ID
     * @return 收藏数量
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 更新最后阅读章节和时间
     * @param userId 用户 ID
     * @param novelId 小说 ID
     * @param chapterId 章节 ID
     * @return 影响的行数
     */
    int updateLastRead(
            @Param("userId") Long userId,
            @Param("novelId") Long novelId,
            @Param("chapterId") Long chapterId);
}
