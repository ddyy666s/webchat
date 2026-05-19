package com.chat.chat_backend.modules.group.controller;

import com.chat.chat_backend.modules.group.entity.GroupMessage;
import com.chat.chat_backend.modules.group.mapper.GroupMessageMapper;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GroupMessageControllerTest {

    @Mock private GroupMessageMapper groupMessageMapper;
    @Mock private UserMapper userMapper;
    @InjectMocks private GroupMessageController groupMessageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupMessageController).build();
    }

    @Test
    void getGroupHistory_shouldReturnPaginated() throws Exception {
        GroupMessage msg = new GroupMessage();
        msg.setId(1L);
        msg.setGroupId(1L);
        msg.setFromUserId(2L);
        msg.setContent("hello");
        msg.setMessageType(1);
        msg.setSendTime(LocalDateTime.now());

        when(groupMessageMapper.countHistory(anyLong())).thenReturn(1);
        when(groupMessageMapper.findHistory(anyLong(), anyInt(), anyInt())).thenReturn(List.of(msg));
        User user = new User();
        user.setNickname("sender");
        when(userMapper.selectById(2L)).thenReturn(user);

        mockMvc.perform(get("/group/message/1").requestAttr("userId", 1L)
                        .param("page", "1").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].id").value(1L))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.current").value(1))
                .andExpect(jsonPath("$.data.size").value(20));
    }

    @Test
    void getGroupHistory_empty_shouldReturnEmptyPage() throws Exception {
        when(groupMessageMapper.countHistory(anyLong())).thenReturn(0);
        when(groupMessageMapper.findHistory(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/group/message/1").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.records").isEmpty());
    }

    @Test
    void getGroupHistory_withRecall_shouldShowRecallText() throws Exception {
        GroupMessage msg = new GroupMessage();
        msg.setId(1L);
        msg.setFromUserId(2L);
        msg.setContent("original");
        msg.setRecallTime(LocalDateTime.now());
        when(groupMessageMapper.countHistory(anyLong())).thenReturn(1);
        when(groupMessageMapper.findHistory(anyLong(), anyInt(), anyInt())).thenReturn(List.of(msg));
        User user = new User();
        user.setNickname("sender");
        when(userMapper.selectById(2L)).thenReturn(user);

        mockMvc.perform(get("/group/message/1").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].content").value("对方撤回了一条消息"));
    }
}
