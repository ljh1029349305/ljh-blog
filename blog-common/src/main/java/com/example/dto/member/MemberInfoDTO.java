package com.example.dto.member;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberInfoDTO {
    private String level;
    private String levelName;
    private LocalDateTime expireTime;
    private Integer levelProgress;
    private Integer nextLevelNeed;
    private String levelColor;
    private Long currentPlan;
}