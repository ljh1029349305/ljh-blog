package com.example.dto.pay;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PayNotifyDTO {
    private String orderId;
    private String status;
    private String payMethod;
    private String transactionId;
    private LocalDateTime payTime;
}