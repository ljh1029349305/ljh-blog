package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    /**
     * 获取用户的文章列表
     */
    @Select("SELECT * FROM article WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<Article> findByUserId(Long userId);


    
    /**
     * 更新文章浏览量
     */
    @Update("UPDATE article SET views = views + 1 WHERE id = #{id}")
    int incrementViews(Long id);
    
    /**
     * 更新文章点赞数
     */
    @Update("UPDATE article SET likes = likes + 1 WHERE id = #{id}")
    int incrementLikes(Long id);
}