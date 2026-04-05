package org.example.novelplatform.service.impl;

import org.example.novelplatform.entity.Chapter;
import org.example.novelplatform.entity.Novel;
import org.example.novelplatform.mapper.ChapterMapper;
import org.example.novelplatform.mapper.NovelMapper;
import org.example.novelplatform.service.ChapterService;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChapterServiceImpl implements ChapterService {

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private NovelMapper novelMapper;

    @Override
    public ResponseMessage<Chapter> getChapterById(Long chapterId) {
        Chapter chapter = chapterMapper.selectByChapterId(chapterId);
        if (chapter == null) {
            return ResponseMessage.error("章节不存在");
        }
        return ResponseMessage.success(chapter);
    }

    @Override
    public ResponseMessage<List<Chapter>> getChaptersByNovelId(Long novelId) {
        List<Chapter> chapters = chapterMapper.selectByNovelId(novelId);
        return ResponseMessage.success(chapters);
    }

    @Override
    public ResponseMessage<Chapter> getChapterByNovelIdAndNum(Long novelId, Integer chapterNum) {
        Chapter chapter = chapterMapper.selectByNovelIdAndChapterNum(novelId, chapterNum);
        if (chapter == null) {
            return ResponseMessage.error("章节不存在");
        }
        return ResponseMessage.success(chapter);
    }

    @Override
    public ResponseMessage<Chapter> getLatestChapter(Long novelId) {
        Chapter chapter = chapterMapper.selectLatestChapter(novelId);
        if (chapter == null) {
            return ResponseMessage.error("暂无章节");
        }
        return ResponseMessage.success(chapter);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage<String> createChapter(Chapter chapter) {
        Novel novel = novelMapper.selectByNovelId(chapter.getNovelId());
        if (novel == null) {
            return ResponseMessage.error("小说不存在");
        }

        chapter.setCreateTime(LocalDateTime.now());
        chapter.setUpdateTime(LocalDateTime.now());

        if (chapter.getWordCount() == null) {
            chapter.setWordCount(chapter.getChapterContent() != null ?
                    chapter.getChapterContent().length() : 0);
        }

        int result = chapterMapper.insertChapter(chapter);
        if (result > 0) {
            updateNovelLastChapter(novel.getNovelId(), chapter);
            return ResponseMessage.success("章节创建成功", "章节创建成功");
        } else {
            return ResponseMessage.error("章节创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage<String> updateChapter(Chapter chapter) {
        Chapter existingChapter = chapterMapper.selectByChapterId(chapter.getChapterId());
        if (existingChapter == null) {
            return ResponseMessage.error("章节不存在");
        }

        chapter.setUpdateTime(LocalDateTime.now());

        if (chapter.getWordCount() == null && chapter.getChapterContent() != null) {
            chapter.setWordCount(chapter.getChapterContent().length());
        }

        int result = chapterMapper.updateChapter(chapter);
        if (result > 0) {
            if (isLatestChapter(existingChapter.getNovelId(), chapter.getChapterId())) {
                updateNovelLastChapter(existingChapter.getNovelId(), chapter);
            }
            return ResponseMessage.success("更新成功", "更新成功");
        } else {
            return ResponseMessage.error("更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage<String> deleteChapter(Long chapterId) {
        Chapter chapter = chapterMapper.selectByChapterId(chapterId);
        if (chapter == null) {
            return ResponseMessage.error("章节不存在");
        }

        int result = chapterMapper.deleteChapter(chapterId);
        if (result > 0) {
            Chapter newLatestChapter = chapterMapper.selectLatestChapter(chapter.getNovelId());
            if (newLatestChapter != null) {
                updateNovelLastChapter(chapter.getNovelId(), newLatestChapter);
            }
            return ResponseMessage.success("删除成功", "删除成功");
        } else {
            return ResponseMessage.error("删除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage<String> deleteBatchChapters(List<Long> chapterIds) {
        if (chapterIds == null || chapterIds.isEmpty()) {
            return ResponseMessage.error("章节 ID 不能为空");
        }

        int result = chapterMapper.deleteBatchChapters(chapterIds);
        if (result > 0) {
            return ResponseMessage.success("批量删除成功", "批量删除成功");
        } else {
            return ResponseMessage.error("批量删除失败");
        }
    }

    @Override
    public ResponseMessage<Integer> getChapterCount(Long novelId) {
        int count = chapterMapper.countByNovelId(novelId);
        return ResponseMessage.success(count);
    }

    private void updateNovelLastChapter(Long novelId, Chapter chapter) {
        Novel novel = new Novel();
        novel.setNovelId(novelId);
        novel.setLastChapterId(chapter.getChapterId());
        novel.setLastChapterName(chapter.getChapterTitle());
        novel.setLastUpdateTime(LocalDateTime.now());
        novelMapper.updateNovel(novel);
    }

    private boolean isLatestChapter(Long novelId, Long chapterId) {
        Chapter latestChapter = chapterMapper.selectLatestChapter(novelId);
        return latestChapter != null && latestChapter.getChapterId().equals(chapterId);
    }
}
