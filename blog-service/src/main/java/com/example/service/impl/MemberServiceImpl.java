package com.example.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.example.dto.member.MemberInfoDTO;
import com.example.dto.member.MemberPlanDTO;
import com.example.entity.Member;
import com.example.entity.MemberOrder;
import com.example.entity.MemberPlan;
import com.example.exception.BusinessException;
import com.example.mapper.MemberMapper;
import com.example.mapper.MemberOrderMapper;
import com.example.mapper.MemberPlanMapper;
import com.example.service.MemberService;
import com.example.util.WxPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private MemberPlanMapper planMapper;
    @Autowired
    private MemberOrderMapper orderMapper;
    @Autowired
    private WxPayUtil wxPayUtil;
    @Override
    public MemberInfoDTO getMemberInfo(Long userId) {
        Member member = memberMapper.getValidMember(userId);
        MemberInfoDTO dto = new MemberInfoDTO();
        if (member != null) {
            dto.setLevel(member.getLevel());
            dto.setLevelName(getLevelName(member.getLevel()));
            dto.setExpireTime(member.getExpireTime());
            dto.setLevelProgress(calculateLevelProgress(member.getLevel()));
            dto.setNextLevelNeed(calculateNextLevelNeed(member.getLevel()));
            dto.setLevelColor(getLevelColor(member.getLevel()));
        } else {
            dto.setLevel("normal");
            dto.setLevelName("普通用户");
            dto.setLevelProgress(0);
            dto.setNextLevelNeed(100);
            dto.setLevelColor("#909399");
        }
        return dto;
    }

    @Override
    public List<MemberPlanDTO> getMemberPlans() {
        List<MemberPlan> plans = planMapper.selectList(null);
        return plans.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String createPayOrder(Long userId, Long planId, String payMethod) {
        // 1. 验证会员套餐
        MemberPlan plan = planMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException("无效的会员套餐");
        }

        // 2. 创建订单
        MemberOrder order = new MemberOrder();
        order.setUserId(userId);
        order.setPlanId(planId);
        order.setOrderNo(generateOrderNo());
        order.setAmount(plan.getPrice());
        order.setStatus("pending");
        order.setPayMethod(payMethod);
        orderMapper.insert(order);

        // 3. 调用支付接口获取支付链接
        return order.getOrderNo();
    }

    @Override
    public String getPayQrCode(String orderId, String payType) {
        MemberOrder order = orderMapper.getByOrderNo(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        try {
            String qrCodeUrl;
            if ("wechat".equals(payType)) {
                qrCodeUrl = generateWxPayQrCode(order);
            } else if ("alipay".equals(payType)) {
                qrCodeUrl = generateAlipayQrCode(order);
            } else {
                throw new BusinessException("不支持的支付方式");
            }
            // 生成二维码图片名称
            String fileName = String.format("%s_%s_%s.png", payType, orderId, System.currentTimeMillis());
            String filePath = qrcodeSavePath + fileName;
            // 生成二维码
            QrCodeUtil.generate(qrCodeUrl, 300, 300, new File(filePath));
            // 返回二维码访问路径
            return qrcodeAccessPath + fileName;
        } catch (Exception e) {
            log.error("生成支付二维码失败", e);
            throw new BusinessException("生成支付二维码失败");
        }
    }

    private String generateWxPayQrCode(MemberOrder order) {
        // 构建统一下单请求参数
        Map<String, String> params = wxPayUtil.createUnifiedOrderParams(
                order.getOrderNo(),
                order.getAmount().multiply(new BigDecimal("100")).intValue() + "", // 转换为分
                "会员购买-" + order.getOrderNo()
        );
        // 调用微信统一下单接口
        String xmlParams = wxPayUtil.mapToXml(params);
        String result = HttpUtil.post("https://api.mch.weixin.qq.com/pay/unifiedorder", xmlParams);
        // 解析返回结果
        // 注意：这里需要添加XML解析逻辑，从返回结果中获取code_url
        // 为了示例简单，这里直接返回模拟的URL
        return "weixin://wxpay/bizpayurl?pr=xxx"; // 实际使用时需要替换为真实的支付URL
    }



    private String generateAlipayQrCode(MemberOrder order) {
        // 支付宝支付逻辑
        return "https://qr.alipay.com/xxx"; // 实际使用时需要替换为真实的支付URL
    }

    @Override
    public boolean checkPayStatus(String orderId) {
        MemberOrder order = orderMapper.getByOrderNo(orderId);
        return order != null && "success".equals(order.getStatus());
    }

    @Override
    @Transactional
    public void handlePaySuccess(String orderId) {
        MemberOrder order = orderMapper.getByOrderNo(orderId);
        if (order == null || !"pending".equals(order.getStatus())) {
            return;
        }
        // 1. 更新订单状态
        order.setStatus("success");
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
        // 2. 获取会员套餐信息
        MemberPlan plan = planMapper.selectById(order.getPlanId());
        // 3. 更新会员信息
        Member member = memberMapper.getValidMember(order.getUserId());
        if (member == null) {
            member = new Member();
            member.setUserId(order.getUserId());
            member.setLevel(plan.getLevel());
            member.setExpireTime(LocalDateTime.now().plusDays(plan.getDuration()));
            memberMapper.insert(member);
        } else {
            member.setLevel(plan.getLevel());
            member.setExpireTime(member.getExpireTime().plusDays(plan.getDuration()));
            memberMapper.updateById(member);
        }
    }

    private String generateOrderNo() {
        return "VIP" + System.currentTimeMillis() + RandomUtil.randomNumbers(6);
    }

    private MemberPlanDTO convertToDTO(MemberPlan plan) {
        MemberPlanDTO dto = new MemberPlanDTO();
        BeanUtils.copyProperties(plan, dto);
        dto.setBenefits(JSONArray.parseArray(plan.getBenefits(), String.class));
        return dto;
    }

    private String getLevelName(String level) {
        switch (level) {
            case "gold": return "黄金会员";
            case "platinum": return "铂金会员";
            case "diamond": return "钻石会员";
            default: return "普通用户";
        }
    }

    private String getLevelColor(String level) {
        switch (level) {
            case "gold": return "#FFD700";
            case "platinum": return "#E5E4E2";
            case "diamond": return "#B9F2FF";
            default: return "#909399";
        }
    }

    private int calculateLevelProgress(String level) {
        switch (level) {
            case "gold": return 30;
            case "platinum": return 60;
            case "diamond": return 100;
            default: return 0;
        }
    }

    private int calculateNextLevelNeed(String level) {
        switch (level) {
            case "gold": return 70;
            case "platinum": return 40;
            case "diamond": return 0;
            default: return 100;
        }
    }
}