package com.chat.chat_backend.modules.notification.service.impl;

import com.chat.chat_backend.modules.notification.dto.response.SystemNotificationVO;
import com.chat.chat_backend.modules.notification.entity.SystemNotification;
import com.chat.chat_backend.modules.notification.mapper.SystemNotificationMapper;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import com.chat.chat_backend.websocket.WebSocketSessionManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.socket.WebSocketSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemNotificationServiceTest {

    @Mock
    private SystemNotificationMapper notificationMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private WebSocketSessionManager sessionManager;
    @InjectMocks
    private SystemNotificationServiceImpl systemNotificationService;

    @Test
    void sendNotification_shouldBroadcastToOnlineUsers() {
        User admin = new User();
        admin.setId(1L);
        admin.setNickname("admin");
        when(userMapper.selectById(1L)).thenReturn(admin);

        ConcurrentHashMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
        sessions.put(2L, mock(WebSocketSession.class));
        sessions.put(3L, mock(WebSocketSession.class));
        when(sessionManager.getAllSessions()).thenReturn(sessions);

        systemNotificationService.sendNotification(1L, "title", "content");

        verify(notificationMapper).insert(any(SystemNotification.class));
        verify(sessionManager).sendMessage(eq(2L), anyString());
        verify(sessionManager).sendMessage(eq(3L), anyString());
    }

    @Test
    void getUnreadNotifications_shouldReturnList() {
        SystemNotification notif = new SystemNotification();
        notif.setId(1L);
        notif.setTitle("title");
        notif.setContent("content");
        notif.setAdminId(1L);
        notif.setCreatedAt(LocalDateTime.now());

        when(notificationMapper.findUnreadByUserId(1L)).thenReturn(List.of(notif));

        User admin = new User();
        admin.setId(1L);
        admin.setNickname("admin");
        when(userMapper.selectById(1L)).thenReturn(admin);

        List<SystemNotificationVO> result = systemNotificationService.getUnreadNotifications(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("title", result.get(0).getTitle());
        assertEquals("admin", result.get(0).getAdminNickname());
    }

    @Test
    void markAsRead_shouldBeIdempotent() {
        when(notificationMapper.existsRead(1L, 1L)).thenReturn(1);

        systemNotificationService.markAsRead(1L, 1L);

        verify(notificationMapper, never()).markAsRead(1L, 1L);
    }

    @Test
    void markAsRead_shouldInsertWhenNotRead() {
        when(notificationMapper.existsRead(1L, 1L)).thenReturn(0);

        systemNotificationService.markAsRead(1L, 1L);

        verify(notificationMapper).markAsRead(1L, 1L);
    }
}
