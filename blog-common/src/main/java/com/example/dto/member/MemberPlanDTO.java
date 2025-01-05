package com.example.dto.member;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MemberPlanDTO {
    private Long id;
    private String name;
    private String type;
    private String level;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private List<String> benefits;
}