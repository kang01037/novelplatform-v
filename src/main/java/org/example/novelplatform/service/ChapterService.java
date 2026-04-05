package org.example.novelplatform.service;

import org.example.novelplatform.entity.Chapter;
import org.example.novelplatform.util.ResponseMessage;

import java.util.List;

public interface ChapterService {

    ResponseMessage<Chapter> getChapterById(Long chapterId);

    ResponseMessage<List<Chapter>> getChaptersByNovelId(Long novelId);

    ResponseMessage<Chapter> getChapterByNovelIdAndNum(Long novelId, Integer chapterNum);

    ResponseMessage<Chapter> getLatestChapter(Long novelId);

    ResponseMessage<String> createChapter(Chapter chapter);

    ResponseMessage<String> updateChapter(Chapter chapter);

    ResponseMessage<String> deleteChapter(Long chapterId);

    ResponseMessage<String> deleteBatchChapters(List<Long> chapterIds);

    ResponseMessage<Integer> getChapterCount(Long novelId);
}
