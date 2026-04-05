package org.example.novelplatform.service;

import org.example.novelplatform.entity.Bookshelf;
import org.example.novelplatform.util.ResponseMessage;

import java.util.List;

public interface BookshelfService {

    ResponseMessage<Bookshelf> getBookshelfById(Long id);

    ResponseMessage<List<Bookshelf>> getBookshelfByUserId(Long userId);

    ResponseMessage<Bookshelf> getBookshelfByUserIdAndNovelId(Long userId, Long novelId);

    ResponseMessage<Boolean> checkIfAdded(Long userId, Long novelId);

    ResponseMessage<String> addToBookshelf(Long userId, Long novelId);

    ResponseMessage<String> removeFromBookshelf(Long userId, Long novelId);

    ResponseMessage<String> removeFromBookshelfBatch(Long userId, List<Long> novelIds);

    ResponseMessage<Integer> getBookshelfCount(Long userId);

    ResponseMessage<String> updateReadingProgress(Long userId, Long novelId, Long chapterId);
}
