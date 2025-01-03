package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleLikeMapper extends BaseMapper<ArticleLike> {

    @Select("SELECT COUNT(*) FROM article_like al " +
            "LEFT JOIN article a ON al.article_id = a.id " +
            "WHERE a.user_id = #{userId} AND a.deleted = 0")
    Long getUserTotalLikes(@Param("userId") Long userId);
}
