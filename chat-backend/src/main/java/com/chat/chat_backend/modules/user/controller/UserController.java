package com.chat.chat_backend.modules.user.controller;

import com.chat.chat_backend.common.result.Result;
import com.chat.chat_backend.modules.user.dto.request.LoginRequest;
import com.chat.chat_backend.modules.user.dto.request.RegisterRequest;
import com.chat.chat_backend.modules.user.dto.request.UpdateProfileRequest;
import com.chat.chat_backend.modules.user.dto.response.LoginResponse;
import com.chat.chat_backend.modules.user.dto.response.UserInfoResponse;
import com.chat.chat_backend.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户控制器，提供注册、登录、信息查询、资料修改、头像上传等接口
 * @author chat-backend
 * @since 2026-05-12
 */
@Tag(name = "用户管理", description = "用户注册、登录、信息查询、资料修改、头像上传等接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     * @param request 注册请求
     * @return 操作结果
     */
    @Operation(summary = "用户注册", description = "使用用户名、密码和昵称注册新用户")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success("注册成功", null);
    }

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应（包含 Token）
     */
    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回JWT令牌和用户信息")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }

    /**
     * 获取当前用户信息
     * @param request HTTP 请求对象（包含用户信息）
     * @return 用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "根据JWT令牌获取当前登录用户的详细信息")
    @GetMapping("/me")
    public Result<UserInfoResponse> getMe(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserInfoResponse response = userService.getUserInfo(userId);
        return Result.success(response);
    }

    /**
     * 更新用户资料
     * @param request HTTP 请求对象（包含用户信息）
     * @param updateRequest 更新资料请求
     * @return 更新后的用户信息
     */
    @Operation(summary = "更新用户资料", description = "更新当前用户的昵称和个性签名")
    @PutMapping("/profile")
    public Result<UserInfoResponse> updateProfile(HttpServletRequest request,
                                                  @RequestBody UpdateProfileRequest updateRequest) {
        Long userId = (Long) request.getAttribute("userId");
        UserInfoResponse response = userService.updateProfile(userId, updateRequest);
        return Result.success(response);
    }

    /**
     * 更新用户头像
     * @param request HTTP 请求对象（包含用户信息）
     * @param file 头像文件
     * @return 头像 URL
     */
    @Operation(summary = "更新用户头像", description = "上传新头像文件并更新当前用户的头像URL")
    @PostMapping("/avatar")
    public Result<String> updateAvatar(HttpServletRequest request,
                                       @RequestParam("file") MultipartFile file) {
        Long userId = (Long) request.getAttribute("userId");
        String avatarUrl = userService.updateAvatar(userId, file);
        return Result.success(avatarUrl);
    }

    /**
     * 获取指定用户的公开资料
     * @param userId 用户ID
     * @return 用户公开资料
     */
    @Operation(summary = "获取用户公开资料", description = "根据用户ID获取指定用户的公开信息")
    @GetMapping("/{userId}")
    public Result<UserInfoResponse> getUserProfile(@PathVariable Long userId) {
        return Result.success(userService.getUserProfile(userId));
    }
}
