package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("member_plan")
public class MemberPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String type;  // monthly/quarterly/yearly
    private String level;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer duration;  // 有效期(天)
    private String benefits;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 