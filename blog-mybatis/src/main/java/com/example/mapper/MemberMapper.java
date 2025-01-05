package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MemberMapper extends BaseMapper<Member> {
    @Select("SELECT * FROM member WHERE user_id = #{userId} AND expire_time > NOW()")
    Member getValidMember(@Param("userId") Long userId);
}