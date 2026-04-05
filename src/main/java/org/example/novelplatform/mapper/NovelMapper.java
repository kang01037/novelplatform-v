package org.example.novelplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.novelplatform.entity.Novel;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface NovelMapper {

    /**
     * 根据小说 ID 查询小说信息
     * @param novelId 小说 ID
     * @return 小说对象，如果不存在则返回 null
     */
    Novel selectByNovelId(@Param("novelId") Long novelId);

    /**
     * 查询所有小说列表
     * @return 小说列表
     */
    List<Novel> selectAllNovels();

    /**
     * 根据作者 ID 查询小说列表
     * @param authorId 作者 ID
     * @return 小说列表
     */
    List<Novel> selectByAuthorId(@Param("authorId") Long authorId);

    /**
     * 根据分类 ID 查询小说列表
     * @param categoryId 分类 ID
     * @return 小说列表
     */
    List<Novel> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 插入新小说
     * @param novel 小说对象
     * @return 影响的行数，大于 0 表示插入成功
     */
    int insertNovel(Novel novel);

    /**
     * 更新小说信息
     * @param novel 小说对象（需要包含 novelId）
     * @return 影响的行数，大于 0 表示更新成功
     */
    int updateNovel(Novel novel);

    /**
     * 删除小说（软删除）
     * @param novelId 小说 ID
     * @return 影响的行数，大于 0 表示删除成功
     */
    int deleteNovel(@Param("novelId") Long novelId);

    /**
     * 增加点击量
     * @param novelId 小说 ID
     * @return 影响的行数，大于 0 表示更新成功
     */
    int incrementClickCount(@Param("novelId") Long novelId);

    /**
     * 增加收藏量
     * @param novelId 小说 ID
     * @return 影响的行数，大于 0 表示更新成功
     */
    int incrementCollectCount(@Param("novelId") Long novelId);

    /**
     * 增加推荐量
     * @param novelId 小说 ID
     * @return 影响的行数，大于 0 表示更新成功
     */
    int incrementRecommendCount(@Param("novelId") Long novelId);

    /**
     * 更新评分
     * @param novelId 小说 ID
     * @param score 新评分
     * @return 影响的行数，大于 0 表示更新成功
     */
    int updateScore(@Param("novelId") Long novelId, @Param("score") BigDecimal score);

    /**
     * 查询热门小说（按点击量排序）
     * @param limit 返回数量限制
     * @return 小说列表
     */
    List<Novel> selectHotNovels(@Param("limit") Integer limit);

    /**
     * 查询最新更新的小说
     * @param limit 返回数量限制
     * @return 小说列表
     */
    List<Novel> selectLatestNovels(@Param("limit") Integer limit);

    /**
     * 根据小说名称模糊查询
     * @param novelName 小说名称（支持模糊匹配）
     * @return 小说列表
     */
    List<Novel> selectByNovelNameLike(@Param("novelName") String novelName);
}
