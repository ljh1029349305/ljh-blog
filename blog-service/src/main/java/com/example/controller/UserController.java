package com.example.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.example.dto.LoginDTO;
import com.example.dto.RegisterDTO;
import com.example.dto.UserUpdateDTO;
import com.example.entity.User;
import com.example.rep.R;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public R login(@RequestBody LoginDTO loginDTO) {
        try {
            // 验证用户名密码
            User user = userService.validateUser(loginDTO);
            if (user == null) {
                return R.error(500,"用户名或密码错误");
            }

            // 登录成功，获取token
            StpUtil.login(user.getId());
            String tokenValue = StpUtil.getTokenValue();

            // 返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", tokenValue);
            data.put("user", user);

            return R.ok(data);
        } catch (Exception e) {
            return R.error(500,"登录失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/register")
    public R register(@RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }
    
    @SaCheckLogin
    @GetMapping("/info")
    public R getUserInfo() {
        return R.ok(userService.getUserInfo());
    }
    
    @SaCheckLogin
    @PutMapping("/update")
    public R updateUserInfo(@RequestBody UserUpdateDTO userUpdateDTO) {
        userService.updateUserInfo(userUpdateDTO);
        return R.ok("更新成功");
    }

    /**
     * 退出登录
     */
    @SaCheckLogin
    @PostMapping("/logout")
    public R logout() {
        try {
            // 获取当前会话账号id
            Long loginId = StpUtil.getLoginIdAsLong();
            // 当前账号注销登录
            StpUtil.logout();
            // 或者使用 StpUtil.logout(loginId); 注销指定账号
            return R.ok("退出成功");
        } catch (Exception e) {
            return R.error(500,"退出失败：" + e.getMessage());
        }
    }
}