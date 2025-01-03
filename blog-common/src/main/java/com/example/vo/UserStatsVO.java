package com.example.vo;

import lombok.Data;

@Data
public class UserStatsVO {
    private Long userId;
    private String username;
    private String avatar;
    private Integer articleCount;  // 文章数
    private Integer followers;     // 粉丝数
    private Integer likes;         // 获赞数
    private Boolean isFollowed;    // 当前用户是否已关注
}