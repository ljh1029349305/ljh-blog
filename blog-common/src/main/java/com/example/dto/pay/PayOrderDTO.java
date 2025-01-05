package com.example.dto.pay;


import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


@Data
public class PayOrderDTO {
    @NotNull(message = "套餐ID不能为空")
    private Long planId;
    
    @NotBlank(message = "支付方式不能为空")
    @Pattern(regexp = "^(wechat|alipay)$", message = "无效的支付方式")
    private String payMethod;
}