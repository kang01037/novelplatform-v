package org.example.novelplatform.service.impl;

import org.example.novelplatform.entity.Novel;
import org.example.novelplatform.exception.ServiceException;
import org.example.novelplatform.mapper.NovelMapper;
import org.example.novelplatform.service.NovelService;
import org.example.novelplatform.util.FileValidator;
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
    public Novel getNovelById(Long novelId) {
        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            throw new ServiceException("小说不存在");
        }
        return novel;
    }

    @Override
    public List<Novel> getAllNovels() {
        return novelMapper.selectAllNovels();
    }

    @Override
    public List<Novel> getNovelsByAuthorId(Long authorId) {
        return novelMapper.selectByAuthorId(authorId);
    }

    @Override
    public List<Novel> getNovelsByCategoryId(Long categoryId) {
        return novelMapper.selectByCategoryId(categoryId);
    }

    @Override
    public Novel createNovel(Novel novel) {
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
            return novelMapper.selectByNovelId(novel.getNovelId());
        } else {
            throw new ServiceException("小说创建失败");
        }
    }

    @Override
    public Novel updateNovel(Novel novel) {
        Novel existingNovel = novelMapper.selectByNovelId(novel.getNovelId());
        if (existingNovel == null) {
            throw new ServiceException("小说不存在");
        }

        novel.setUpdatedTime(LocalDateTime.now());
        int result = novelMapper.updateNovel(novel);
        if (result > 0) {
            return novelMapper.selectByNovelId(novel.getNovelId());
        } else {
            throw new ServiceException("更新失败");
        }
    }

    @Override
    public void deleteNovel(Long novelId) {
        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            throw new ServiceException("小说不存在");
        }

        int result = novelMapper.deleteNovel(novelId);
        if (result <= 0) {
            throw new ServiceException("删除失败");
        }
    }

    @Override
    public void incrementClickCount(Long novelId) {
        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            throw new ServiceException("小说不存在");
        }

        int result = novelMapper.incrementClickCount(novelId);
        if (result <= 0) {
            throw new ServiceException("更新失败");
        }
    }

    @Override
    public void incrementCollectCount(Long novelId) {
        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            throw new ServiceException("小说不存在");
        }

        int result = novelMapper.incrementCollectCount(novelId);
        if (result <= 0) {
            throw new ServiceException("更新失败");
        }
    }

    @Override
    public void incrementRecommendCount(Long novelId) {
        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            throw new ServiceException("小说不存在");
        }

        int result = novelMapper.incrementRecommendCount(novelId);
        if (result <= 0) {
            throw new ServiceException("更新失败");
        }
    }

    @Override
    public void rateNovel(Long novelId, BigDecimal score) {
        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            throw new ServiceException("小说不存在");
        }

        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.TEN) > 0) {
            throw new ServiceException("评分必须在 0-10 之间");
        }

        int result = novelMapper.updateScore(novelId, score);
        if (result <= 0) {
            throw new ServiceException("评分失败");
        }
    }

    @Override
    public List<Novel> getHotNovels(Integer limit) {
        return novelMapper.selectHotNovels(limit);
    }

    @Override
    public List<Novel> getLatestNovels(Integer limit) {
        return novelMapper.selectLatestNovels(limit);
    }

    @Override
    public List<Novel> searchNovels(String novelName) {
        return novelMapper.selectByNovelNameLike(novelName);
    }

    @Override
    public String uploadCover(Long novelId, MultipartFile file) {
        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            throw new ServiceException("小说不存在");
        }

        if (file.isEmpty()) {
            throw new ServiceException("上传文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches(".*\\.(jpg|jpeg|png|gif|webp)$")) {
            throw new ServiceException("只支持 jpg、jpeg、png、gif、webp 格式的图片");
        }

        if (!FileValidator.validateImageFile(file)) {
            throw new ServiceException("文件内容校验失败，请上传有效的图片文件");
        }

        long fileSize = file.getSize();
        if (fileSize > 5 * 1024 * 1024) {
            throw new ServiceException("文件大小不能超过 5MB");
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
                return coverUrl;
            } else {
                Files.deleteIfExists(filePath);
                throw new ServiceException("封面上传失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException("封面上传失败");
        }
    }
}
