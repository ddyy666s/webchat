package com.chat.chat_backend.modules.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.chat_backend.modules.admin.dto.response.StatisticsVO;
import com.chat.chat_backend.modules.admin.dto.response.UserManageVO;
import com.chat.chat_backend.modules.admin.service.AdminService;
import com.chat.chat_backend.modules.message.dto.response.MessageAuditVO;
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
class AdminControllerTest {

    @Mock
    private AdminService adminService;
    @Mock
    private SystemNotificationService systemNotificationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AdminController(adminService, systemNotificationService)).build();
    }

    @Test
    void getNotifications_shouldReturnList() throws Exception {
        when(systemNotificationService.getNotificationsByAdmin(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/admin/notifications")
                .requestAttr("userId", 1L).requestAttr("role", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(systemNotificationService).getNotificationsByAdmin(1L);
    }

    @Test
    void getNotifications_shouldFailWhenNotAdmin() throws Exception {
        mockMvc.perform(get("/admin/notifications")
                .requestAttr("userId", 1L).requestAttr("role", "user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("无权限"));

        verify(systemNotificationService, never()).getNotificationsByAdmin(anyLong());
    }

    @Test
    void getUserList_shouldReturnPage() throws Exception {
        when(adminService.getUserList(anyInt(), anyInt(), isNull()))
                .thenReturn(new Page<UserManageVO>(1, 10));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adminService).getUserList(1, 10, null);
    }

    @Test
    void updateUserStatus_shouldSucceed() throws Exception {
        mockMvc.perform(put("/admin/user/2/status").param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adminService).updateUserStatus(2L, 1);
    }

    @Test
    void getMessageList_shouldReturnPage() throws Exception {
        when(adminService.getMessageList(anyInt(), anyInt(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(new Page<MessageAuditVO>(1, 20));

        mockMvc.perform(get("/admin/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(adminService).getMessageList(1, 20, null, null, null, null);
    }

    @Test
    void getStatistics_shouldReturnStats() throws Exception {
        when(adminService.getStatistics()).thenReturn(StatisticsVO.builder().totalUsers(100L).build());

        mockMvc.perform(get("/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalUsers").value(100));

        verify(adminService).getStatistics();
    }
}
