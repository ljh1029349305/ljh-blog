package com.example.service;

import com.example.dto.member.MemberInfoDTO;
import com.example.dto.member.MemberPlanDTO;

import java.util.List;

public interface MemberService {
    MemberInfoDTO getMemberInfo(Long userId);
    List<MemberPlanDTO> getMemberPlans();
    String createPayOrder(Long userId, Long planId, String payMethod);
    String getPayQrCode(String orderId, String payType);
    boolean checkPayStatus(String orderId);
    void handlePaySuccess(String orderId);
}