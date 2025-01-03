package com.example.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.entity.UserFollow;
import com.example.mapper.UserFollowMapper;
import com.example.rep.R;
import com.example.service.UserService;
import com.example.vo.UserStatsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/author")
@Slf4j
public class AuthorController {
    
    @Autowired
    private UserService userService;
    @Autowired
    private UserFollowMapper userFollowMapper;

    
    /**
     * 获取作者信息和统计数据
     */
    @GetMapping("/info/{authorId}")
    public R getAuthorInfo(@PathVariable Long authorId) {
        try {
            UserStatsVO stats = userService.getUserStats(authorId);
            return R.ok(stats);
        } catch (Exception e) {
            return R.error(500,"获取作者信息失败");
        }
    }
    
    /**
     * 关注/取消关注作者
     */
    @SaCheckLogin
    @PostMapping("/{authorId}/follow")
    public R followAuthor(@PathVariable Long authorId) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            if (userId.equals(authorId)) {
                return R.error(500,"不能关注自己");
            }
            
            LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<UserFollow>()
                .eq(UserFollow::getUserId, userId)
                .eq(UserFollow::getFollowId, authorId);
                
            UserFollow follow = userFollowMapper.selectOne(wrapper);
            
            if (follow == null) {
                // 添加关注
                follow = new UserFollow();
                follow.setUserId(userId);
                follow.setFollowId(authorId);
                userFollowMapper.insert(follow);
                return R.ok(true);
            } else {
                // 取消关注
                userFollowMapper.delete(wrapper);
                return R.ok(false);
            }
        } catch (Exception e) {
            return R.error(500,"操作失败");
        }
    }
}