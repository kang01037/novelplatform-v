package org.example.novelplatformv.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Novel {

    private Long novelId;

    private String novelName;

    private Long authorId;

    private Long categoryId;

    private String coverImage;

    private String content;

    private Integer novelStatus;

    private Long clickCount;

    private Long collectCount;

    private Long recommendCount;

    private BigDecimal score;

    private Integer scoreCount;

    private LocalDateTime lastUpdateTime;

    private Long lastChapterId;

    private String lastChapterName;

    private Integer status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private Integer deleted;
}
