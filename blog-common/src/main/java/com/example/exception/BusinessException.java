package com.example.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    private int code;
    
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }
    
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}