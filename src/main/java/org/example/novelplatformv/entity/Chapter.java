package org.example.novelplatformv.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Chapter {

    private Long chapterId;

    private Long novelId;

    private String novelName;

    private Integer chapterNum;

    private String chapterTitle;

    private String chapterContent;

    private Integer wordCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
