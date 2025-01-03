package com.example.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.ArticleDTO;
import com.example.dto.ArticleQueryDTO;
import com.example.entity.Article;
import com.example.rep.R;
import com.example.service.ArticleService;
import com.example.vo.ArticleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/article")
@Slf4j
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;

    @GetMapping("/getArticles")
    public R getArticles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<ArticleVO> result = articleService.getArticleList(page, size);
            return R.ok().setData(result);
        } catch (Exception e) {
            return R.error("获取文章列表失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/getArticle/{id}")
    public R getArticleDetail(@PathVariable Long id) {
        return articleService.getArticleDetail(id);
    }
    

    /**
     * 获取我的文章列表
     */
    @SaCheckLogin
    @GetMapping("/getMyArticles")
    public R getMyArticles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<Article> result = articleService.getMyArticles(page, size);
            return R.ok().setData(result);
        } catch (Exception e) {
            log.error("获取文章列表失败", e);
            return R.error("获取文章列表失败");
        }
    }

    /**
     * 创建文章
     */
    @SaCheckLogin
    @PostMapping("/createArticle")
    public R createArticle(@RequestBody ArticleDTO articleDTO) {
        try {
            Article article = articleService.createArticle(articleDTO);
            return R.ok().setData(article);
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("创建文章失败", e);
            return R.error("创建文章失败");
        }
    }


    /**
     * 获取文章点赞状态
     */
    @SaCheckLogin
    @GetMapping("/getLikeStatus/{articleId}")
    public R getLikeStatus(@PathVariable Long articleId) {
        try {
            // 获取当前用户ID
            Long userId = StpUtil.getLoginIdAsLong();
            boolean isLiked = articleService.isArticleLiked(articleId, userId);
            return R.ok().setData(isLiked);
        } catch (Exception e) {
            log.error("获取点赞状态失败", e);
            return R.error("获取点赞状态失败");
        }
    }

    /**
     * 点赞/取消点赞文章
     */
    @SaCheckLogin
    @PostMapping("/likeArticle/{articleId}")
    public R likeArticle(@PathVariable Long articleId) {
        try {
            // 获取当前用户ID
            Long userId = StpUtil.getLoginIdAsLong();
            boolean isLiked = articleService.toggleArticleLike(articleId, userId);
            return R.ok().setData(isLiked);
        } catch (Exception e) {
            log.error("操作失败", e);
            return R.error("操作失败");
        }
    }

    /**
     * 更新文章
     */
    @SaCheckLogin
    @PutMapping("/updateArticle/{id}")
    public R updateArticle(@PathVariable Long id, @RequestBody ArticleDTO articleDTO) {
        try {
            Article article = articleService.updateArticle(id, articleDTO);
            return R.ok().setData(article);
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("更新文章失败", e);
            return R.error("更新文章失败");
        }
    }

    /**
     * 删除文章
     */
    @SaCheckLogin
    @DeleteMapping("/deleteArticle/{id}")
    public R deleteArticle(@PathVariable Long id) {
        try {
            articleService.deleteArticle(id);
            return R.ok("删除成功");
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("删除文章失败", e);
            return R.error("删除文章失败");
        }
    }



}