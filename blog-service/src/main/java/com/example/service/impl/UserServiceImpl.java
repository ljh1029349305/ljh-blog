package com.example.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.dto.user.LoginDTO;
import com.example.dto.user.RegisterDTO;
import com.example.dto.user.UserUpdateDTO;
import com.example.entity.Article;
import com.example.entity.User;
import com.example.entity.UserFollow;
import com.example.exception.BusinessException;
import com.example.mapper.ArticleLikeMapper;
import com.example.mapper.ArticleMapper;
import com.example.mapper.UserFollowMapper;
import com.example.mapper.UserMapper;
import com.example.rep.R;
import com.example.service.UserService;
import com.example.vo.UserInfoVO;
import com.example.vo.UserStatsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ArticleLikeMapper articleLikeMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserFollowMapper userFollowMapper;

    @Override
    public R login(LoginDTO loginDTO) {
        Object loginId = StpUtil.getLoginId();

        // 查询用户
        User user = userMapper.selectOne(
            new QueryWrapper<User>()
                .eq("username", loginDTO.getUsername())
        );

        if (user == null) {
            return R.error(500,"用户不存在");
        }

        // 验证密码
        String encryptPassword = SaSecureUtil.md5(loginDTO.getPassword());
        if (!encryptPassword.equals(user.getPassword())) {
            return R.error(500,"密码错误");
        }
        // 登录
        StpUtil.login(user.getId());

        /*HashMap<Object, Object> tokenMap = new HashMap<>();
        tokenMap.put("token", StpUtil.getTokenValue());*/
        HashMap<Object, Object> userMap = new HashMap<>();
        userMap.put("user", StpUtil.getTokenValue());
        return R.ok(userMap);
            /*.setData(tokenMap)
            .setData(userMap);*/
    }
    @Override
    public R logout() {
        try {
            // 可以在这里添加一些额外的清理工作
            // 比如清除用户的一些缓存数据等
            StpUtil.logout();
            return R.ok("退出成功");
        } catch (Exception e) {
            return R.error(500,"退出失败");
        }
    }
    
    @Override
    public R register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userMapper.selectCount(new QueryWrapper<User>()
            .eq("username", registerDTO.getUsername())) > 0) {
            return R.error(500,"用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userMapper.selectCount(new QueryWrapper<User>()
            .eq("email", registerDTO.getEmail())) > 0) {
            return R.error(500,"邮箱已被使用");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        // 使用 Sa-Token 的加密工具
        user.setPassword(SaSecureUtil.md5(registerDTO.getPassword()));
        userMapper.insert(user);
        return R.ok("注册成功");
    }

    @Override
    public UserInfoVO getUserInfo() {
        String loginId = StpUtil.getLoginId().toString();
        User user = userMapper.selectById(Long.parseLong(loginId));
        if (user != null) {
            UserInfoVO userInfoVO = new UserInfoVO();
            BeanUtils.copyProperties(user, userInfoVO);
            return userInfoVO;
        }
        return null;
    }

    @Override
    public void updateUserInfo(UserUpdateDTO userUpdateDTO) {
        // 根据传入的DTO中的id获取数据库中对应的用户记录
        User user = userMapper.selectById(userUpdateDTO.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 将DTO中的参数值赋给获取到的用户对象，实现部分字段更新
        user.setAvatar(userUpdateDTO.getAvatar());
        user.setEmail(userUpdateDTO.getEmail());
        user.setBio(userUpdateDTO.getBio());
        // 创建QueryWrapper，这里其实可以为空，因为我们已经通过selectById获取到具体要更新的用户记录了
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userUpdateDTO.getId());
        // 调用userMapper的update方法进行更新
        int updateRows = userMapper.update(user, queryWrapper);
        if (updateRows < 0) {
            throw new BusinessException("更新失败");
        }

    }

    @Override
    public User validateUser(LoginDTO loginDTO) {
        if (loginDTO == null || StringUtils.isBlank(loginDTO.getUsername())
                || StringUtils.isBlank(loginDTO.getPassword())) {
            throw new RuntimeException("用户名或密码不能为空");
        }
        // 查询用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, loginDTO.getUsername())

        );
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 验证密码
        String encryptPassword = SaSecureUtil.md5(loginDTO.getPassword());
        if (!encryptPassword.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // 清除敏感信息
        user.setPassword(null);
        return user;
    }
    @Override
    public UserStatsVO getUserStats(Long userId) {
        // 获取用户基本信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserStatsVO stats = new UserStatsVO();
        stats.setUserId(userId);
        stats.setUsername(user.getUsername());
        stats.setAvatar(user.getAvatar());

        // 获取文章数
        Long articleCount = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getUserId, userId)
                        .eq(Article::getDeleted, false)
        );
        stats.setArticleCount(articleCount.intValue());

        // 获取粉丝数
        Long followerCount = userFollowMapper.selectCount(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getFollowId, userId)
        );
        stats.setFollowers(followerCount.intValue());

        // 获取获赞数
        Long likeCount = articleLikeMapper.getUserTotalLikes(userId);
        stats.setLikes(likeCount.intValue());

        // 判断当前用户是否已关注
        if (StpUtil.isLogin()) {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (!currentUserId.equals(userId)) {
                boolean isFollowed = userFollowMapper.exists(
                        new LambdaQueryWrapper<UserFollow>()
                                .eq(UserFollow::getUserId, currentUserId)
                                .eq(UserFollow::getFollowId, userId)
                );
                stats.setIsFollowed(isFollowed);
            }
        }

        return stats;
    }


}