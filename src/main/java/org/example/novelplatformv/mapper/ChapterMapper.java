package org.example.novelplatformv.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.novelplatformv.entity.Chapter;

import java.util.List;

@Mapper
public interface ChapterMapper {

    /**
     * 根据章节 ID 查询章节信息
     * @param chapterId 章节 ID
     * @return 章节对象，如果不存在则返回 null
     */
    Chapter selectByChapterId(@Param("chapterId") Long chapterId);

    /**
     * 根据小说 ID 查询所有章节列表
     * @param novelId 小说 ID
     * @return 章节列表
     */
    List<Chapter> selectByNovelId(@Param("novelId") Long novelId);

    /**
     * 根据小说 ID 和章节号查询章节
     * @param novelId 小说 ID
     * @param chapterNum 章节号
     * @return 章节对象，如果不存在则返回 null
     */
    Chapter selectByNovelIdAndChapterNum(
            @Param("novelId") Long novelId,
            @Param("chapterNum") Integer chapterNum);

    /**
     * 查询小说的最新章节
     * @param novelId 小说 ID
     * @return 章节对象，如果不存在则返回 null
     */
    Chapter selectLatestChapter(@Param("novelId") Long novelId);

    /**
     * 插入新章节
     * @param chapter 章节对象
     * @return 影响的行数，大于 0 表示插入成功
     */
    int insertChapter(Chapter chapter);

    /**
     * 更新章节信息
     * @param chapter 章节对象（需要包含 chapterId）
     * @return 影响的行数，大于 0 表示更新成功
     */
    int updateChapter(Chapter chapter);

    /**
     * 删除章节
     * @param chapterId 章节 ID
     * @return 影响的行数，大于 0 表示删除成功
     */
    int deleteChapter(@Param("chapterId") Long chapterId);

    /**
     * 批量删除章节
     * @param chapterIds 章节 ID 列表
     * @return 影响的行数
     */
    int deleteBatchChapters(@Param("chapterIds") List<Long> chapterIds);

    /**
     * 统计小说章节数量
     * @param novelId 小说 ID
     * @return 章节数量
     */
    int countByNovelId(@Param("novelId") Long novelId);
}
