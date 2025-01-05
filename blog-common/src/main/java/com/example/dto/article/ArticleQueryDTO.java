package com.example.dto.article;

import lombok.Data;

@Data
public class ArticleQueryDTO {
    private Integer page = 1;
    private Integer size = 10;
}