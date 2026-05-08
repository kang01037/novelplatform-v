package org.example.novelplatform.service;

import org.example.novelplatform.entity.Bookshelf;

import java.util.List;

public interface BookshelfService {

    Bookshelf getBookshelfById(Long id);

    List<Bookshelf> getBookshelfByUserId(Long userId);

    Bookshelf getBookshelfByUserIdAndNovelId(Long userId, Long novelId);

    boolean checkIfAdded(Long userId, Long novelId);

    Bookshelf addToBookshelf(Long userId, Long novelId);

    void removeFromBookshelf(Long userId, Long novelId);

    void removeFromBookshelfBatch(Long userId, List<Long> novelIds);

    int getBookshelfCount(Long userId);

    Bookshelf updateReadingProgress(Long userId, Long novelId, Long chapterId);
}
