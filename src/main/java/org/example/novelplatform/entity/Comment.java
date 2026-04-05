package org.example.novelplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long commentId;

    private Long userId;

    private Long novelId;

    private Long chapterId;

    private Long parentId;

    private String content;

    private Integer likeCount;

    private Integer replyCount;

    private Integer status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private Integer deleted;
}
