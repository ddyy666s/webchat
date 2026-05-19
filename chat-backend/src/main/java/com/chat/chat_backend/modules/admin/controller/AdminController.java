package com.chat.chat_backend.modules.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.chat_backend.common.result.Result;
import com.chat.chat_backend.modules.message.dto.response.MessageAuditVO;
import com.chat.chat_backend.modules.admin.dto.response.StatisticsVO;
import com.chat.chat_backend.modules.notification.dto.response.SystemNotificationVO;
import com.chat.chat_backend.modules.admin.dto.response.UserManageVO;
import com.chat.chat_backend.modules.admin.service.AdminService;
import com.chat.chat_backend.modules.notification.service.SystemNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 管理员控制器
 * @author chat-backend
 * @since 2026-05-12
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "管理员相关接口")
public class AdminController {

    private final AdminService adminService;
    private final SystemNotificationService systemNotificationService;

    @GetMapping("/notifications")
    @Operation(summary = "获取管理员已发送的通知列表")
    public Result<List<SystemNotificationVO>> getNotifications(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            return Result.error("无权限");
        }
        Long adminId = (Long) request.getAttribute("userId");
        List<SystemNotificationVO> list = systemNotificationService.getNotificationsByAdmin(adminId);
        return Result.success(list);
    }

    @GetMapping("/users")
    @Operation(summary = "获取用户列表")
    public Result<Page<UserManageVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<UserManageVO> result = adminService.getUserList(page, size, keyword);
        return Result.success(result);
    }

    @PutMapping("/user/{userId}/status")
    @Operation(summary = "禁用/启用用户")
    public Result<Void> updateUserStatus(@PathVariable Long userId, @RequestParam Integer status) {
        adminService.updateUserStatus(userId, status);
        return Result.success("操作成功", null);
    }

    @GetMapping("/messages")
    @Operation(summary = "获取聊天记录（审计）")
    public Result<Page<MessageAuditVO>> getMessageList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Long fromUserId,
            @RequestParam(required = false) Long toUserId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        Page<MessageAuditVO> result = adminService.getMessageList(page, size, fromUserId, toUserId, startTime, endTime);
        return Result.success(result);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取统计数据")
    public Result<StatisticsVO> getStatistics() {
        StatisticsVO result = adminService.getStatistics();
        return Result.success(result);
    }
}
