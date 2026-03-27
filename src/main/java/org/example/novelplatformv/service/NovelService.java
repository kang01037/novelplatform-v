package org.example.novelplatformv.service;

import org.example.novelplatformv.entity.Novel;
import org.example.novelplatformv.util.ResponseMessage;

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
}
