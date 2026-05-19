package com.chat.chat_backend.modules.notification.controller;

import com.chat.chat_backend.modules.notification.service.SystemNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SystemNotificationControllerTest {

    @Mock
    private SystemNotificationService systemNotificationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SystemNotificationController(systemNotificationService)).build();
    }

    @Test
    void sendNotification_shouldSucceedAsAdmin() throws Exception {
        mockMvc.perform(post("/system-notification/send")
                .requestAttr("userId", 1L)
                .requestAttr("role", "admin")
                .contentType("application/json")
                .content("{\"title\":\"通知标题\",\"content\":\"通知内容\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(systemNotificationService).sendNotification(1L, "通知标题", "通知内容");
    }

    @Test
    void sendNotification_shouldFailWhenNotAdmin() throws Exception {
        mockMvc.perform(post("/system-notification/send")
                .requestAttr("userId", 1L)
                .requestAttr("role", "user")
                .contentType("application/json")
                .content("{\"title\":\"通知标题\",\"content\":\"通知内容\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("只有管理员才能发送系统通知"));

        verify(systemNotificationService, never()).sendNotification(anyLong(), anyString(), anyString());
    }

    @Test
    void getUnreadNotifications_shouldReturnList() throws Exception {
        when(systemNotificationService.getUnreadNotifications(1L)).thenReturn(List.of());
        when(systemNotificationService.getUnreadCount(1L)).thenReturn(0);

        mockMvc.perform(get("/system-notification/unread").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(systemNotificationService).getUnreadNotifications(1L);
        verify(systemNotificationService).getUnreadCount(1L);
    }

    @Test
    void markAsRead_shouldCallService() throws Exception {
        mockMvc.perform(put("/system-notification/read/5").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(systemNotificationService).markAsRead(1L, 5L);
    }
}
