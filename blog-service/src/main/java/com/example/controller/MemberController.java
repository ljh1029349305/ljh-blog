package com.example.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.dto.member.MemberBenefitDTO;
import com.example.dto.member.MemberInfoDTO;
import com.example.dto.member.MemberPlanDTO;
import com.example.dto.pay.PayNotifyDTO;
import com.example.dto.pay.PayOrderDTO;
import com.example.rep.R;
import com.example.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/member")
@Slf4j
public class MemberController {
    @Autowired
    private MemberService memberService;

    @GetMapping("/info")
    public R getMemberInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        MemberInfoDTO memberInfo = memberService.getMemberInfo(userId);
        return R.ok(memberInfo, "获取会员信息成功");
    }

    @GetMapping("/plans")
    public R getMemberPlans() {
        List<MemberPlanDTO> plans = memberService.getMemberPlans();
        return R.ok(plans, "获取会员套餐成功");
    }

    @PostMapping("/pay/create")
    public R createPayOrder(@RequestBody @Validated PayOrderDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        String orderId = memberService.createPayOrder(userId, dto.getPlanId(), dto.getPayMethod());
        Map<String, String> data = Collections.singletonMap("orderId", orderId);
        return R.ok(data, "创建支付订单成功");
    }

    @GetMapping("/pay/qrcode/{orderId}")
    public R getPayQrCode(@PathVariable String orderId, @RequestParam String payType) {
        String qrCode = memberService.getPayQrCode(orderId, payType);
        return R.ok(qrCode, "获取支付二维码成功");
    }

    @GetMapping("/pay/status/{orderId}")
    public R checkPayStatus(@PathVariable String orderId) {
        boolean success = memberService.checkPayStatus(orderId);
        Map<String, String> data = Collections.singletonMap("status", success ? "success" : "pending");
        return R.ok(data, "查询支付状态成功");
    }

    // 支付回调接口
    @PostMapping("/pay/notify")
    public R handlePayNotify(@RequestBody PayNotifyDTO notify) {
        log.info("收到支付回调：{}", notify);
        memberService.handlePaySuccess(notify.getOrderId());
        return R.ok(null, "支付回调处理成功");
    }


    @GetMapping("/benefits")
    public R getMemberBenefits() {
        List<MemberBenefitDTO> benefits = Arrays.asList(
                new MemberBenefitDTO(1L, "专属标识", "彰显尊贵身份的专属等级标识", "el-icon-trophy"),
                new MemberBenefitDTO(2L, "文章数量提升", "每日发文数量限制大幅提升", "el-icon-document"),
                new MemberBenefitDTO(3L, "自定义头像框", "独特的会员专属头像框", "el-icon-picture"),
                new MemberBenefitDTO(4L, "文章置顶", "文章可享置顶特权", "el-icon-top"),
                new MemberBenefitDTO(5L, "数据分析", "专业的文章数据分析", "el-icon-data-analysis"),
                new MemberBenefitDTO(6L, "优先响应", "获得优先的技术支持", "el-icon-chat-dot-square")
        );
        return R.ok(benefits, "获取会员权益成功");
    }

    @PostMapping("/pay/notify/wx")
    public String handleWxPayNotify(@RequestBody String xmlData) {
        log.info("收到微信支付回调：{}", xmlData);
        try {
            // 验证签名
            // 解析XML数据
            // 验证支付金额
            // 处理支付结果
            memberService.handlePaySuccess("订单号");
            // 返回成功响应
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        } catch (Exception e) {
            log.error("处理微信支付回调失败", e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
        }
    }

}