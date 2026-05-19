package com.chat.chat_backend.modules.message.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.chat_backend.modules.message.dto.response.MessageVO;
import com.chat.chat_backend.modules.message.dto.response.UnreadCountVO;
import com.chat.chat_backend.modules.message.service.MessageService;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
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
class MessageControllerTest {

    @Mock
    private MessageService messageService;
    @Mock
    private UserMapper userMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MessageController(messageService, userMapper)).build();
    }

    @Test
    void getChatHistory_shouldReturnPage() throws Exception {
        when(messageService.getChatHistory(anyLong(), anyLong(), anyInt(), anyInt()))
                .thenReturn(new Page<MessageVO>(1, 20));

        mockMvc.perform(get("/message/history/2").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(messageService).getChatHistory(1L, 2L, 1, 20);
    }

    @Test
    void markAsRead_shouldCallService() throws Exception {
        mockMvc.perform(put("/message/read/2").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("已标记已读"));

        verify(messageService).markAsRead(1L, 2L);
    }

    @Test
    void getUnreadCount_shouldReturnCount() throws Exception {
        when(messageService.getUnreadCount(anyLong()))
                .thenReturn(UnreadCountVO.builder().total(5).details(List.of()).messages(List.of()).build());

        mockMvc.perform(get("/message/unread/count").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(5));

        verify(messageService).getUnreadCount(1L);
    }

    @Test
    void recallMessage_shouldCallService() throws Exception {
        mockMvc.perform(put("/message/recall/100").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(messageService).recallMessage(1L, 100L);
    }
}
