package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.MemberPlan;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberPlanMapper extends BaseMapper<MemberPlan> {
}