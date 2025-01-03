package com.example.service;

import com.example.dto.LoginDTO;
import com.example.dto.RegisterDTO;
import com.example.dto.UserUpdateDTO;
import com.example.entity.User;
import com.example.rep.R;
import com.example.vo.UserStatsVO;

public interface UserService {
    /**
     * 用户登录
     */
    R login(LoginDTO loginDTO);
    
    /**
     * 用户注册
     */
    R register(RegisterDTO registerDTO);
    
    /**
     * 获取用户信息
     */
    R getUserInfo();
    
    /**
     * 更新用户信息
     */
    R updateUserInfo(UserUpdateDTO userUpdateDTO);

    R logout();

    /**
     * 验证用户登录
     * @param loginDTO 登录信息
     * @return 用户信息
     */
    User validateUser(LoginDTO loginDTO);

    /**
     * 获取用户统计信息
     */
    UserStatsVO getUserStats(Long userId);
}
