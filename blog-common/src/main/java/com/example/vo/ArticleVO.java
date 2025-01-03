package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleVO {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private Integer views;
    private Integer likes;
    private LocalDateTime createTime;
    
    // 作者信息
    private String authorName;
    private String authorAvatar;
}