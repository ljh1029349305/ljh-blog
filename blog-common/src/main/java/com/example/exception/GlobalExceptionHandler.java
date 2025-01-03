package com.example.exception;

import com.example.rep.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder message = new StringBuilder();
        
        for (FieldError error : fieldErrors) {
            message.append(error.getField())
                   .append(": ")
                   .append(error.getDefaultMessage())
                   .append("; ");
        }
        
        log.error("参数校验失败：{}", message);
        return R.error(500,message.toString());
    }

    /**
     * 处理唯一约束异常
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R handleSQLException(SQLIntegrityConstraintViolationException e) {
        log.error("数据库操作异常：{}", e.getMessage());
        
        if (e.getMessage().contains("Duplicate entry")) {
            String[] split = e.getMessage().split(" ");
            String message = split[2] + "已存在";
            return R.error(500,message);
        }
        
        return R.error(500,"数据库操作失败");
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public R handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return R.error(500,"业务异常");
    }

    /**
     * 处理权限异常
     */
    @ExceptionHandler(cn.dev33.satoken.exception.NotLoginException.class)
    public R handleNotLoginException(cn.dev33.satoken.exception.NotLoginException e) {
        log.error("权限异常：{}", e.getMessage());
        return R.error(401, "请先登录");
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        log.error("系统异常：", e);
        return R.error(500,"系统异常，请联系管理员");
    }
}