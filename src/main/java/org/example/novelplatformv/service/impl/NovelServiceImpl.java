package org.example.novelplatformv.service.impl;

import org.example.novelplatformv.entity.Novel;
import org.example.novelplatformv.mapper.NovelMapper;
import org.example.novelplatformv.service.NovelService;
import org.example.novelplatformv.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NovelServiceImpl implements NovelService {

    @Autowired
    private NovelMapper novelMapper;

    @Override
    public ResponseMessage<Novel> getNovelById(Long novelId) {
        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            return ResponseMessage.error("小说不存在");
        }
        return ResponseMessage.success(novel);
    }

    @Override
    public ResponseMessage<List<Novel>> getAllNovels() {
        List<Novel> novels = novelMapper.selectAllNovels();
        return ResponseMessage.success(novels);
    }

    @Override
    public ResponseMessage<List<Novel>> getNovelsByAuthorId(Long authorId) {
        List<Novel> novels = novelMapper.selectByAuthorId(authorId);
        return ResponseMessage.success(novels);
    }

    @Override
    public ResponseMessage<List<Novel>> getNovelsByCategoryId(Long categoryId) {
        List<Novel> novels = novelMapper.selectByCategoryId(categoryId);
        return ResponseMessage.success(novels);
    }

    @Override
    public ResponseMessage<String> createNovel(Novel novel) {
        novel.setCreatedTime(LocalDateTime.now());
        novel.setUpdatedTime(LocalDateTime.now());
        novel.setDeleted(0);
        novel.setClickCount(0L);
        novel.setCollectCount(0L);
        novel.setRecommendCount(0L);
        novel.setScore(BigDecimal.valueOf(0.00));
        novel.setScoreCount(0);
        novel.setStatus(1);

        int result = novelMapper.insertNovel(novel);
        if (result > 0) {
            return ResponseMessage.success("小说创建成功");
        } else {
            return ResponseMessage.error("小说创建失败");
        }
    }

    @Override
    public ResponseMessage<String> updateNovel(Novel novel) {
        Novel existingNovel = novelMapper.selectByNovelId(novel.getNovelId());
        if (existingNovel == null) {
            return ResponseMessage.error("小说不存在");
        }

        novel.setUpdatedTime(LocalDateTime.now());
        int result = novelMapper.updateNovel(novel);
        if (result > 0) {
            return ResponseMessage.success("更新成功");
        } else {
            return ResponseMessage.error("更新失败");
        }
    }

    @Override
    public ResponseMessage<String> deleteNovel(Long novelId) {
        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            return ResponseMessage.error("小说不存在");
        }

        int result = novelMapper.deleteNovel(novelId);
        if (result > 0) {
            return ResponseMessage.success("删除成功");
        } else {
            return ResponseMessage.error("删除失败");
        }
    }

    @Override
    public ResponseMessage<String> incrementClickCount(Long novelId) {
        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            return ResponseMessage.error("小说不存在");
        }

        int result = novelMapper.incrementClickCount(novelId);
        if (result > 0) {
            return ResponseMessage.success("点击量已更新");
        } else {
            return ResponseMessage.error("更新失败");
        }
    }

    @Override
    public ResponseMessage<String> incrementCollectCount(Long novelId) {
        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            return ResponseMessage.error("小说不存在");
        }

        int result = novelMapper.incrementCollectCount(novelId);
        if (result > 0) {
            return ResponseMessage.success("收藏量已更新");
        } else {
            return ResponseMessage.error("更新失败");
        }
    }

    @Override
    public ResponseMessage<String> incrementRecommendCount(Long novelId) {
        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            return ResponseMessage.error("小说不存在");
        }

        int result = novelMapper.incrementRecommendCount(novelId);
        if (result > 0) {
            return ResponseMessage.success("推荐量已更新");
        } else {
            return ResponseMessage.error("更新失败");
        }
    }

    @Override
    public ResponseMessage<String> rateNovel(Long novelId, BigDecimal score) {
        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            return ResponseMessage.error("小说不存在");
        }

        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.TEN) > 0) {
            return ResponseMessage.error("评分必须在 0-10 之间");
        }

        int result = novelMapper.updateScore(novelId, score);
        if (result > 0) {
            return ResponseMessage.success("评分成功");
        } else {
            return ResponseMessage.error("评分失败");
        }
    }

    @Override
    public ResponseMessage<List<Novel>> getHotNovels(Integer limit) {
        List<Novel> novels = novelMapper.selectHotNovels(limit);
        return ResponseMessage.success(novels);
    }

    @Override
    public ResponseMessage<List<Novel>> getLatestNovels(Integer limit) {
        List<Novel> novels = novelMapper.selectLatestNovels(limit);
        return ResponseMessage.success(novels);
    }

    @Override
    public ResponseMessage<List<Novel>> searchNovels(String novelName) {
        List<Novel> novels = novelMapper.selectByNovelNameLike(novelName);
        return ResponseMessage.success(novels);
    }
}
