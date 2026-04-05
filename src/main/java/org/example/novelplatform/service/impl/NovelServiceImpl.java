package org.example.novelplatform.service.impl;

import org.example.novelplatform.entity.Novel;
import org.example.novelplatform.mapper.NovelMapper;
import org.example.novelplatform.service.NovelService;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
            return ResponseMessage.success("小说创建成功", "小说创建成功");
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
            return ResponseMessage.success("更新成功", "更新成功");
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
            return ResponseMessage.success("删除成功", "删除成功");
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
            return ResponseMessage.success("点击量已更新", "点击量已更新");
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
            return ResponseMessage.success("收藏量已更新", "收藏量已更新");
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
            return ResponseMessage.success("推荐量已更新", "推荐量已更新");
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
            return ResponseMessage.success("评分成功", "评分成功");
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

    @Override
    public ResponseMessage<String> uploadCover(Long novelId, MultipartFile file) {
        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            return ResponseMessage.error("小说不存在");
        }

        if (file.isEmpty()) {
            return ResponseMessage.error("上传文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches(".*\\.(jpg|jpeg|png|gif|webp)$")) {
            return ResponseMessage.error("只支持 jpg、jpeg、png、gif、webp 格式的图片");
        }

        long fileSize = file.getSize();
        if (fileSize > 5 * 1024 * 1024) {
            return ResponseMessage.error("文件大小不能超过 5MB");
        }

        String uploadDir = System.getProperty("user.dir") + "/uploads/covers/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;
        Path filePath = Paths.get(uploadDir + newFilename);

        try {
            Files.write(filePath, file.getBytes());

            String coverUrl = "/api/novel/cover/" + newFilename;
            novel.setCoverImage(coverUrl);
            novel.setUpdatedTime(LocalDateTime.now());

            int result = novelMapper.updateNovel(novel);
            if (result > 0) {
                return ResponseMessage.success("封面上传成功", "封面上传成功");
            } else {
                Files.deleteIfExists(filePath);
                return ResponseMessage.error("封面上传失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseMessage.error("封面上传失败：" + e.getMessage());
        }
    }
}
