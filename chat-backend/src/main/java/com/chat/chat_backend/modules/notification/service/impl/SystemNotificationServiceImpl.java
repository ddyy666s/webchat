package com.chat.chat_backend.modules.notification.service.impl;

import cn.hutool.json.JSONUtil;
import com.chat.chat_backend.modules.notification.dto.response.SystemNotificationVO;
import com.chat.chat_backend.modules.notification.entity.SystemNotification;
import com.chat.chat_backend.modules.notification.mapper.SystemNotificationMapper;
import com.chat.chat_backend.modules.notification.service.SystemNotificationService;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import com.chat.chat_backend.websocket.WebSocketSessionManager;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Schema(description = "系统通知服务实现")
public class SystemNotificationServiceImpl implements SystemNotificationService {

    private final SystemNotificationMapper notificationMapper;
    private final UserMapper userMapper;
    private final WebSocketSessionManager sessionManager;

    @Override
    @Transactional
    @Schema(description = "发送系统通知（持久化后通过WebSocket推送给所有在线用户）")
    public void sendNotification(Long adminId, String title, String content) {
        SystemNotification notification = new SystemNotification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setAdminId(adminId);
        notification.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);

        User admin = userMapper.selectById(adminId);
        String adminNickname = admin != null ? admin.getNickname() : "系统管理员";

        String jsonMessage = JSONUtil.createObj()
                .set("type", "notification")
                .set("notificationId", notification.getId())
                .set("title", title)
                .set("content", content)
                .set("adminNickname", adminNickname)
                .set("createdAt", notification.getCreatedAt().toString())
                .toString();

        for (Long userId : sessionManager.getAllSessions().keySet()) {
            if (!userId.equals(adminId)) {
                sessionManager.sendMessage(userId, jsonMessage);
            }
        }

        log.info("系统通知已发送: title={}, adminId={}", title, adminId);
    }

    @Override
    @Schema(description = "获取用户未读的系统通知列表")
    public List<SystemNotificationVO> getUnreadNotifications(Long userId) {
        List<SystemNotification> notifications = notificationMapper.findUnreadByUserId(userId);
        log.debug("查询未读系统通知: userId={}, count={}", userId, notifications.size());
        return notifications.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Schema(description = "获取指定管理员发送的通知列表")
    public List<SystemNotificationVO> getNotificationsByAdmin(Long adminId) {
        List<SystemNotification> notifications = notificationMapper.findByAdminId(adminId);
        log.debug("查询管理员通知: adminId={}, count={}", adminId, notifications.size());
        return notifications.stream().map(this::toVO).collect(Collectors.toList());
    }

    private SystemNotificationVO toVO(SystemNotification n) {
        User admin = userMapper.selectById(n.getAdminId());
        return SystemNotificationVO.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .adminId(n.getAdminId())
                .adminNickname(admin != null ? admin.getNickname() : "系统管理员")
                .createdAt(n.getCreatedAt())
                .build();
    }

    @Override
    @Schema(description = "获取用户未读通知数量")
    public Integer getUnreadCount(Long userId) {
        Integer count = notificationMapper.countUnreadByUserId(userId);
        log.debug("查询未读通知数量: userId={}, count={}", userId, count);
        return count;
    }

    @Override
    @Schema(description = "标记系统通知为已读（幂等操作）")
    public void markAsRead(Long userId, Long notificationId) {
        int exists = notificationMapper.existsRead(notificationId, userId);
        if (exists == 0) {
            notificationMapper.markAsRead(notificationId, userId);
            log.info("系统通知标记已读: userId={}, notificationId={}", userId, notificationId);
        } else {
            log.debug("系统通知已标记过已读: userId={}, notificationId={}", userId, notificationId);
        }
    }
}
