package org.example.novelplatform.service.impl;

import org.example.novelplatform.entity.Chapter;
import org.example.novelplatform.entity.Novel;
import org.example.novelplatform.exception.ServiceException;
import org.example.novelplatform.mapper.ChapterMapper;
import org.example.novelplatform.mapper.NovelMapper;
import org.example.novelplatform.service.ChapterService;
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
    public Chapter getChapterById(Long chapterId) {
        Chapter chapter = chapterMapper.selectByChapterId(chapterId);
        if (chapter == null) {
            throw new ServiceException("章节不存在");
        }
        return chapter;
    }

    @Override
    public List<Chapter> getChaptersByNovelId(Long novelId) {
        return chapterMapper.selectByNovelId(novelId);
    }

    @Override
    public Chapter getChapterByNovelIdAndNum(Long novelId, Integer chapterNum) {
        Chapter chapter = chapterMapper.selectByNovelIdAndChapterNum(novelId, chapterNum);
        if (chapter == null) {
            throw new ServiceException("章节不存在");
        }
        return chapter;
    }

    @Override
    public Chapter getLatestChapter(Long novelId) {
        Chapter chapter = chapterMapper.selectLatestChapter(novelId);
        if (chapter == null) {
            throw new ServiceException("暂无章节");
        }
        return chapter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Chapter createChapter(Chapter chapter) {
        Novel novel = novelMapper.selectByNovelId(chapter.getNovelId());
        if (novel == null) {
            throw new ServiceException("小说不存在");
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
            return chapterMapper.selectByChapterId(chapter.getChapterId());
        } else {
            throw new ServiceException("章节创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Chapter updateChapter(Chapter chapter) {
        Chapter existingChapter = chapterMapper.selectByChapterId(chapter.getChapterId());
        if (existingChapter == null) {
            throw new ServiceException("章节不存在");
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
            return chapterMapper.selectByChapterId(chapter.getChapterId());
        } else {
            throw new ServiceException("更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChapter(Long chapterId) {
        Chapter chapter = chapterMapper.selectByChapterId(chapterId);
        if (chapter == null) {
            throw new ServiceException("章节不存在");
        }

        int result = chapterMapper.deleteChapter(chapterId);
        if (result <= 0) {
            throw new ServiceException("删除失败");
        }

        Chapter newLatestChapter = chapterMapper.selectLatestChapter(chapter.getNovelId());
        if (newLatestChapter != null) {
            updateNovelLastChapter(chapter.getNovelId(), newLatestChapter);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchChapters(List<Long> chapterIds) {
        if (chapterIds == null || chapterIds.isEmpty()) {
            throw new ServiceException("章节 ID 不能为空");
        }

        int result = chapterMapper.deleteBatchChapters(chapterIds);
        if (result <= 0) {
            throw new ServiceException("批量删除失败");
        }
    }

    @Override
    public int getChapterCount(Long novelId) {
        return chapterMapper.countByNovelId(novelId);
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
