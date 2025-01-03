package com.example.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoVO {

    private Long id;
    private String username;
    private String email;
    private String bio;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}