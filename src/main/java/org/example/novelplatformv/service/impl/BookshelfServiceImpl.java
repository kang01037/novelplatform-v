package org.example.novelplatformv.service.impl;

import org.example.novelplatformv.entity.Bookshelf;
import org.example.novelplatformv.entity.Novel;
import org.example.novelplatformv.mapper.BookshelfMapper;
import org.example.novelplatformv.mapper.NovelMapper;
import org.example.novelplatformv.service.BookshelfService;
import org.example.novelplatformv.util.ResponseMessage;
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
    public ResponseMessage<Bookshelf> getBookshelfById(Long id) {
        Bookshelf bookshelf = bookshelfMapper.selectById(id);
        if (bookshelf == null) {
            return ResponseMessage.error("书架记录不存在");
        }
        return ResponseMessage.success(bookshelf);
    }

    @Override
    public ResponseMessage<List<Bookshelf>> getBookshelfByUserId(Long userId) {
        List<Bookshelf> bookshelves = bookshelfMapper.selectByUserId(userId);
        return ResponseMessage.success(bookshelves);
    }

    @Override
    public ResponseMessage<Bookshelf> getBookshelfByUserIdAndNovelId(Long userId, Long novelId) {
        Bookshelf bookshelf = bookshelfMapper.selectByUserIdAndNovelId(userId, novelId);
        if (bookshelf == null) {
            return ResponseMessage.error("未收藏该小说");
        }
        return ResponseMessage.success(bookshelf);
    }

    @Override
    public ResponseMessage<Boolean> checkIfAdded(Long userId, Long novelId) {
        boolean isAdded = bookshelfMapper.isAddedToBookshelf(userId, novelId);
        return ResponseMessage.success(isAdded);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage<String> addToBookshelf(Long userId, Long novelId) {
        if (userId == null || novelId == null) {
            return ResponseMessage.error("参数不能为空");
        }

        boolean isAdded = bookshelfMapper.isAddedToBookshelf(userId, novelId);
        if (isAdded) {
            return ResponseMessage.error("已在书架中");
        }

        Novel novel = novelMapper.selectByNovelId(novelId);
        if (novel == null) {
            return ResponseMessage.error("小说不存在");
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
            return ResponseMessage.success("加入书架成功");
        } else {
            return ResponseMessage.error("加入书架失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage<String> removeFromBookshelf(Long userId, Long novelId) {
        Bookshelf bookshelf = bookshelfMapper.selectByUserIdAndNovelId(userId, novelId);
        if (bookshelf == null) {
            return ResponseMessage.error("未在书架中找到该小说");
        }

        int result = bookshelfMapper.deleteFromBookshelf(userId, novelId);
        if (result > 0) {
            Novel novel = novelMapper.selectByNovelId(novelId);
            if (novel != null && novel.getCollectCount() != null && novel.getCollectCount() > 0) {
                novelMapper.updateNovel(new Novel() {{
                    setNovelId(novelId);
                    setCollectCount(novel.getCollectCount() - 1);
                }});
            }
            return ResponseMessage.success("移出书架成功");
        } else {
            return ResponseMessage.error("移出书架失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage<String> removeFromBookshelfBatch(Long userId, List<Long> novelIds) {
        if (novelIds == null || novelIds.isEmpty()) {
            return ResponseMessage.error("小说 ID 列表不能为空");
        }

        int result = bookshelfMapper.deleteBatchFromBookshelf(userId, novelIds);
        if (result > 0) {
            for (Long novelId : novelIds) {
                Novel novel = novelMapper.selectByNovelId(novelId);
                if (novel != null && novel.getCollectCount() != null && novel.getCollectCount() > 0) {
                    novelMapper.updateNovel(new Novel() {{
                        setNovelId(novelId);
                        setCollectCount(novel.getCollectCount() - 1);
                    }});
                }
            }
            return ResponseMessage.success("批量移出书架成功");
        } else {
            return ResponseMessage.error("批量移出书架失败");
        }
    }

    @Override
    public ResponseMessage<Integer> getBookshelfCount(Long userId) {
        int count = bookshelfMapper.countByUserId(userId);
        return ResponseMessage.success(count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage<String> updateReadingProgress(Long userId, Long novelId, Long chapterId) {
        Bookshelf bookshelf = bookshelfMapper.selectByUserIdAndNovelId(userId, novelId);
        if (bookshelf == null) {
            return ResponseMessage.error("未在书架中找到该小说");
        }

        int result = bookshelfMapper.updateLastRead(userId, novelId, chapterId);
        if (result > 0) {
            return ResponseMessage.success("阅读进度已更新");
        } else {
            return ResponseMessage.error("更新阅读进度失败");
        }
    }
}
