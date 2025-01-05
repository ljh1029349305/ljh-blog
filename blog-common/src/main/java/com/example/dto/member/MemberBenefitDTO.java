package com.example.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberBenefitDTO {
    private Long id;
    private String title;
    private String description;
    private String icon;
}