package org.example.novelplatform.service;

import org.example.novelplatform.entity.Novel;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface NovelService {

    Novel getNovelById(Long novelId);

    List<Novel> getAllNovels();

    List<Novel> getNovelsByAuthorId(Long authorId);

    List<Novel> getNovelsByCategoryId(Long categoryId);

    Novel createNovel(Novel novel);

    Novel updateNovel(Novel novel);

    void deleteNovel(Long novelId);

    void incrementClickCount(Long novelId);

    void incrementCollectCount(Long novelId);

    void incrementRecommendCount(Long novelId);

    void rateNovel(Long novelId, BigDecimal score);

    List<Novel> getHotNovels(Integer limit);

    List<Novel> getLatestNovels(Integer limit);

    List<Novel> searchNovels(String novelName);

    String uploadCover(Long novelId, MultipartFile file);
}
