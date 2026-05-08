package org.example.novelplatform.service;

import org.example.novelplatform.entity.Chapter;

import java.util.List;

public interface ChapterService {

    Chapter getChapterById(Long chapterId);

    List<Chapter> getChaptersByNovelId(Long novelId);

    Chapter getChapterByNovelIdAndNum(Long novelId, Integer chapterNum);

    Chapter getLatestChapter(Long novelId);

    Chapter createChapter(Chapter chapter);

    Chapter updateChapter(Chapter chapter);

    void deleteChapter(Long chapterId);

    void deleteBatchChapters(List<Long> chapterIds);

    int getChapterCount(Long novelId);
}
