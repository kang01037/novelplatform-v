package org.example.novelplatform.service.impl;

import org.example.novelplatform.entity.Bookshelf;
import org.example.novelplatform.entity.Novel;
import org.example.novelplatform.exception.ServiceException;
import org.example.novelplatform.mapper.BookshelfMapper;
import org.example.novelplatform.mapper.NovelMapper;
import org.example.novelplatform.service.BookshelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookshelfServiceImpl implements BookshelfService {

    @Autowired
    private BookshelfMapper bookshelfMapper;

    @Autowired
    private NovelMapper novelMapper;

    @Override
    public Bookshelf getBookshelfById(Long id) {
        Bookshelf bookshelf = bookshelfMapper.selectById(id);
        if (bookshelf == null) {
            throw new ServiceException("书架记录不存在");
        }
        return bookshelf;
    }

    @Override
    public List<Bookshelf> getBookshelfByUserId(Long userId) {
        return bookshelfMapper.selectByUserId(userId);
    }

    @Override
    public Bookshelf getBookshelfByUserIdAndNovelId(Long userId, Long novelId) {
        Bookshelf bookshelf = bookshelfMapper.selectByUserIdAndNovelId(userId, novelId);
        if (bookshelf == null) {
            throw new ServiceException("未收藏该小说");
        }
        return bookshelf;
    }

    @Override
    public boolean checkIfAdded(Long userId, Long novelId) {
        return bookshelfMapper.isAddedToBookshelf(userId, novelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Bookshelf addToBookshelf(Long userId, Long novelId) {
        if (userId == null || novelId == null) {
            throw new ServiceException("参数不能为空");
        }

        boolean isAdded = bookshelfMapper.isAddedToBookshelf(userId, novelId);
        if (isAdded) {
            throw new ServiceException("已在书架中");
        }

        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            throw new ServiceException("小说不存在");
        }

        Bookshelf bookshelf = new Bookshelf();
        bookshelf.setUserId(userId);
        bookshelf.setNovelId(novelId);
        bookshelf.setLastReadChapterId(null);
        bookshelf.setLastReadTime(null);
        bookshelf.setCreatedTime(LocalDateTime.now());
        bookshelf.setUpdatedTime(LocalDateTime.now());

        int result = bookshelfMapper.insertBookshelf(bookshelf);
        if (result > 0) {
            novelMapper.incrementCollectCount(novelId);
            return bookshelfMapper.selectByUserIdAndNovelId(userId, novelId);
        } else {
            throw new ServiceException("加入书架失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFromBookshelf(Long userId, Long novelId) {
        Bookshelf bookshelf = bookshelfMapper.selectByUserIdAndNovelId(userId, novelId);
        if (bookshelf == null) {
            throw new ServiceException("未在书架中找到该小说");
        }

        int result = bookshelfMapper.deleteFromBookshelf(userId, novelId);
        if (result > 0) {
            Novel novel = novelMapper.selectByNovelId(novelId);
            if (novel != null && novel.getCollectCount() != null && novel.getCollectCount() > 0) {
                Novel updateNovel = new Novel();
                updateNovel.setNovelId(novelId);
                updateNovel.setCollectCount(novel.getCollectCount() - 1);
                novelMapper.updateNovel(updateNovel);
            }
        } else {
            throw new ServiceException("移出书架失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFromBookshelfBatch(Long userId, List<Long> novelIds) {
        if (novelIds == null || novelIds.isEmpty()) {
            throw new ServiceException("小说 ID 列表不能为空");
        }

        int result = bookshelfMapper.deleteBatchFromBookshelf(userId, novelIds);
        if (result > 0) {
            for (Long novelId : novelIds) {
                Novel novel = novelMapper.selectByNovelId(novelId);
                if (novel != null && novel.getCollectCount() != null && novel.getCollectCount() > 0) {
                    Novel updateNovel = new Novel();
                    updateNovel.setNovelId(novelId);
                    updateNovel.setCollectCount(novel.getCollectCount() - 1);
                    novelMapper.updateNovel(updateNovel);
                }
            }
        } else {
            throw new ServiceException("批量移出书架失败");
        }
    }

    @Override
    public int getBookshelfCount(Long userId) {
        return bookshelfMapper.countByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Bookshelf updateReadingProgress(Long userId, Long novelId, Long chapterId) {
        Bookshelf bookshelf = bookshelfMapper.selectByUserIdAndNovelId(userId, novelId);
        if (bookshelf == null) {
            throw new ServiceException("未在书架中找到该小说");
        }

        int result = bookshelfMapper.updateLastRead(userId, novelId, chapterId);
        if (result > 0) {
            return bookshelfMapper.selectByUserIdAndNovelId(userId, novelId);
        } else {
            throw new ServiceException("更新阅读进度失败");
        }
    }
}
