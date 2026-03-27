package org.example.novelplatformv.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Bookshelf {

    private Long id;

    private Long userId;

    private Long novelId;

    private Long lastReadChapterId;

    private LocalDateTime lastReadTime;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
