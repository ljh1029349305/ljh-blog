package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.ArticleDTO;
import com.example.entity.Article;
import com.example.rep.R;
import com.example.vo.ArticleVO;

import java.util.List;

public interface ArticleService {
    /**
     * 获取文章列表
     */
    Page<ArticleVO> getArticleList(Integer page, Integer size);
    
    /**
     * 获取文章详情
     */
    Article getArticleDetail(Long id);
    
    /**
     * 点赞文章
     */
    boolean isArticleLiked(Long articleId, Long userId);

    boolean toggleArticleLike(Long articleId, Long userId);

    /**
     * 获取我的文章列表
     */
    Page<Article> getMyArticles(Integer page, Integer size);

    /**
     * 创建文章
     */
    Article createArticle(ArticleDTO articleDTO);

    /**
     * 更新文章
     */
    Article updateArticle(Long id, ArticleDTO articleDTO);

    /**
     * 删除文章
     */
    void deleteArticle(Long id);


    List<Article> hotArticles(Integer page, Integer size);
}