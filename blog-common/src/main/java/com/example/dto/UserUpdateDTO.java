package com.example.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private Long id;
    private String email;
    private String bio;
    private String avatar;
}
