package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("member_order")
public class MemberOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long planId;
    private String orderNo;
    private BigDecimal amount;
    private String status;  // pending/success/failed/cancelled
    private String payMethod;  // wechat/alipay
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 