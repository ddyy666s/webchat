package com.chat.chat_backend.modules.user.service.impl;

import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.common.result.ResultCode;
import com.chat.chat_backend.common.utils.OssUtil;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import com.chat.chat_backend.modules.user.dto.request.UpdateProfileRequest;
import com.chat.chat_backend.modules.user.dto.response.UserInfoResponse;
import com.chat.chat_backend.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 用户资料服务实现，处理资料修改、头像上传、资料查询等业务
 * @author chat-backend
 * @since 2026-05-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl {

    private final UserMapper userMapper;
    private final OssUtil ossUtil;

    /**
     * 更新用户昵称和个性签名
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    @Transactional
    public UserInfoResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (request.getNickname() != null && !request.getNickname().isEmpty()) {
            user.setNickname(request.getNickname());
        }
        if (request.getSignature() != null) {
            user.setSignature(request.getSignature());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户 {} 资料更新成功", userId);
        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .signature(user.getSignature())
                .role(user.getRole())
                .build();
    }

    /**
     * 通过OSS上传更新用户头像
     * @param userId 用户ID
     * @param file 头像文件
     * @return 头像URL地址
     */
    @Transactional
    public String updateAvatar(Long userId, MultipartFile file) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        try {
            String avatarUrl = ossUtil.uploadFile(file, "avatar/");

            user.setAvatar(avatarUrl);
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);

            log.info("用户 {} 头像更新成功: {}", userId, avatarUrl);
            return avatarUrl;
        } catch (IOException e) {
            log.error("头像上传失败", e);
            throw new BusinessException("头像上传失败");
        }
    }

    /**
     * 获取用户公开资料
     * @param userId 用户ID
     * @return 用户公开资料
     */
    public UserInfoResponse getUserProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .signature(user.getSignature())
                .role(user.getRole())
                .build();
    }
}
