package com.example.dto;

import lombok.Data;

@Data
public class ArticleDTO {
    private String title;
    private String content;
    private String summary;  // 文章摘要，可以自动从内容中提取
}