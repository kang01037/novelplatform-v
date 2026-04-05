package org.example.novelplatform.service;

import org.example.novelplatform.entity.Novel;
import org.example.novelplatform.util.ResponseMessage;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface NovelService {

    ResponseMessage<Novel> getNovelById(Long novelId);

    ResponseMessage<List<Novel>> getAllNovels();

    ResponseMessage<List<Novel>> getNovelsByAuthorId(Long authorId);

    ResponseMessage<List<Novel>> getNovelsByCategoryId(Long categoryId);

    ResponseMessage<String> createNovel(Novel novel);

    ResponseMessage<String> updateNovel(Novel novel);

    ResponseMessage<String> deleteNovel(Long novelId);

    ResponseMessage<String> incrementClickCount(Long novelId);

    ResponseMessage<String> incrementCollectCount(Long novelId);

    ResponseMessage<String> incrementRecommendCount(Long novelId);

    ResponseMessage<String> rateNovel(Long novelId, BigDecimal score);

    ResponseMessage<List<Novel>> getHotNovels(Integer limit);

    ResponseMessage<List<Novel>> getLatestNovels(Integer limit);

    ResponseMessage<List<Novel>> searchNovels(String novelName);

    ResponseMessage<String> uploadCover(Long novelId, MultipartFile file);

}
