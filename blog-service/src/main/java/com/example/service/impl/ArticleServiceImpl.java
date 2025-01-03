package com.example.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dto.ArticleDTO;
import com.example.entity.Article;
import com.example.entity.ArticleLike;
import com.example.entity.User;
import com.example.exception.BusinessException;
import com.example.mapper.ArticleLikeMapper;
import com.example.mapper.ArticleMapper;
import com.example.mapper.UserMapper;
import com.example.rep.R;
import com.example.service.ArticleService;
import com.example.vo.ArticleVO;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {
    
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ArticleLikeMapper articleLikeMapper;

    @Override
    public Page<ArticleVO> getArticleList(Integer page, Integer size) {
        // 构建分页查询
        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .orderByDesc(Article::getCreateTime);

        // 执行分页查询
        Page<Article> articlePage = articleMapper.selectPage(pageParam, wrapper);

        // 转换为VO对象
        Page<ArticleVO> resultPage = new Page<>(page, size, articlePage.getTotal());
        List<ArticleVO> records = articlePage.getRecords().stream().map(article -> {
            ArticleVO vo = new ArticleVO();
            BeanUtils.copyProperties(article, vo);

            // 获取作者信息
            User author = userMapper.selectById(article.getUserId());
            if (author != null) {
                vo.setAuthorName(author.getUsername());
                vo.setAuthorAvatar(author.getAvatar());
            }

            return vo;
        }).collect(Collectors.toList());

        resultPage.setRecords(records);
        return resultPage;
    }

    @Override
    public List<Article> hotArticles(Integer page, Integer size) {
        // 构建分页查询

        // 执行分页查询
        List<Article> hotArticlesPage = articleMapper.hotArticles(page, size);

        return hotArticlesPage;
    }
    
    @Override
    public Article getArticleDetail(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        
        // 增加浏览量
        article.setViews(article.getViews() + 1);
        articleMapper.updateById(article);
        
        return article;
    }


    @Override
    public boolean isArticleLiked(Long articleId, Long userId) {
        return articleLikeMapper.exists(
                new LambdaQueryWrapper<ArticleLike>()
                        .eq(ArticleLike::getArticleId, articleId)
                        .eq(ArticleLike::getUserId, userId)
        );
    }

    @Override
    @Transactional
    public boolean toggleArticleLike(Long articleId, Long userId) {
        // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }

        // 检查是否已点赞
        LambdaQueryWrapper<ArticleLike> wrapper = new LambdaQueryWrapper<ArticleLike>()
                .eq(ArticleLike::getArticleId, articleId)
                .eq(ArticleLike::getUserId, userId);

        ArticleLike like = articleLikeMapper.selectOne(wrapper);

        if (like == null) {
            // 未点赞，添加点赞记录
            like = new ArticleLike();
            like.setArticleId(articleId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            articleLikeMapper.insert(like);

            // 更新文章点赞数
            article.setLikes(article.getLikes() + 1);
            articleMapper.updateById(article);
            return true;
        } else {
            // 已点赞，取消点赞
            articleLikeMapper.delete(wrapper);

            // 更新文章点赞数
            article.setLikes(article.getLikes() - 1);
            articleMapper.updateById(article);
            return false;
        }
    }


    @Override
    public Page<Article> getMyArticles(Integer page, Integer size) {
        // 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();

        // 构建分页查询
        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getUserId, userId)
                .orderByDesc(Article::getCreateTime);

        // 执行查询并返回结果
        return articleMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Article createArticle(ArticleDTO articleDTO) {
        if (isEmpty(articleDTO.getTitle()) || isEmpty(articleDTO.getContent())) {
            throw new IllegalArgumentException("标题和内容不能为空");
        }

        // 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();

        // 创建文章对象
        Article article = new Article();
        article.setUserId(userId);
        article.setTitle(articleDTO.getTitle());
        article.setContent(articleDTO.getContent());
        article.setCreateTime(LocalDateTime.now());

        // 如果没有提供摘要，从内容中提取
        if (isEmpty(articleDTO.getSummary())) {
            String content = articleDTO.getContent().trim();
            String summary = content.length() > 200 ? content.substring(0, 200) : content;
            article.setSummary(summary);
        } else {
            article.setSummary(articleDTO.getSummary());
        }

        // 初始化浏览量和点赞数
        article.setViews(0);
        article.setLikes(0);

        // 保存文章
        articleMapper.insert(article);

        return article;
    }

    // 辅助方法
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    @Override
    public Article updateArticle(Long id, ArticleDTO articleDTO) {
        // 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();

        // 查询文章
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }

        // 验证文章所有权
        if (!article.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此文章");
        }

        // 更新文章
        article.setTitle(articleDTO.getTitle());
        article.setContent(articleDTO.getContent());
        if (StringUtils.isNotBlank(articleDTO.getSummary())) {
            article.setSummary(articleDTO.getSummary());
        }

        articleMapper.updateById(article);

        return article;
    }

    @Override
    public void deleteArticle(Long id) {
        // 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();

        // 查询文章
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }

        // 验证文章所有权
        if (!article.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此文章");
        }

        // 删除文章（软删除）
        articleMapper.deleteById(id);
    }



}