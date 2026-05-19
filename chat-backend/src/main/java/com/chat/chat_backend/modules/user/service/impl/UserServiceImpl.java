package com.chat.chat_backend.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chat.chat_backend.common.constant.RedisConstants;
import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.common.result.ResultCode;
import com.chat.chat_backend.common.utils.JwtUtil;
import com.chat.chat_backend.common.utils.RedisUtil;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import com.chat.chat_backend.modules.user.dto.request.LoginRequest;
import com.chat.chat_backend.modules.user.dto.request.RegisterRequest;
import com.chat.chat_backend.modules.user.dto.request.UpdateProfileRequest;
import com.chat.chat_backend.modules.user.dto.response.LoginResponse;
import com.chat.chat_backend.modules.user.dto.response.UserInfoResponse;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现，处理注册、登录、信息查询等核心认证业务
 * @author chat-backend
 * @since 2026-05-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserProfileServiceImpl userProfileService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /** 用户注册（校验用户名唯一性，BCrypt加密存储密码） @param request 注册请求 */
    @Override
    public void register(RegisterRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User existUser = userMapper.selectOne(wrapper);
        if (existUser != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setRole("user");
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        log.info("用户注册成功: {}", request.getUsername());
    }

    /** 用户登录验证 @param request 登录请求 @return 登录响应 */
    @Override
    public LoginResponse login(LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        redisUtil.addToSet(RedisConstants.ONLINE_USERS, String.valueOf(user.getId()));
        redisUtil.expire(RedisConstants.ONLINE_USERS, RedisConstants.ONLINE_EXPIRE_SECONDS, TimeUnit.SECONDS);
        UserInfoResponse userInfo = UserInfoResponse.builder()
                .id(user.getId()).username(user.getUsername()).nickname(user.getNickname())
                .avatar(user.getAvatar()).signature(user.getSignature()).role(user.getRole())
                .build();
        log.info("用户登录成功: {}", request.getUsername());
        return LoginResponse.builder().token(token).user(userInfo).build();
    }

    /** 根据用户ID获取用户信息 @param userId 用户ID @return 用户信息响应 */
    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return UserInfoResponse.builder()
                .id(user.getId()).username(user.getUsername()).nickname(user.getNickname())
                .avatar(user.getAvatar()).signature(user.getSignature()).role(user.getRole())
                .build();
    }

    @Override
    public UserInfoResponse updateProfile(Long userId, UpdateProfileRequest request) {
        return userProfileService.updateProfile(userId, request);
    }

    @Override
    public String updateAvatar(Long userId, MultipartFile file) {
        return userProfileService.updateAvatar(userId, file);
    }

    @Override
    public UserInfoResponse getUserProfile(Long userId) {
        return userProfileService.getUserProfile(userId);
    }
}
