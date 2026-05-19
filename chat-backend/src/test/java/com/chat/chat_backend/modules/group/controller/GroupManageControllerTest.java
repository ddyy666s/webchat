package com.chat.chat_backend.modules.group.controller;

import com.chat.chat_backend.modules.group.service.GroupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GroupManageControllerTest {

    @Mock @Qualifier("groupManageServiceImpl") private GroupService groupManageService;
    @InjectMocks private GroupManageController groupManageController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupManageController).build();
    }

    @Test
    void updateNotice_shouldSucceed() throws Exception {
        mockMvc.perform(put("/group/1/notice").requestAttr("userId", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("notice", "hello"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("群公告已更新"));
        verify(groupManageService).updateNotice(eq(1L), eq(1L), eq("hello"));
    }

    @Test
    void setAdmin_shouldSucceed() throws Exception {
        mockMvc.perform(put("/group/1/member/2/set-admin").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("已设置管理员"));
        verify(groupManageService).setAdmin(eq(1L), eq(1L), eq(2L));
    }

    @Test
    void removeAdmin_shouldSucceed() throws Exception {
        mockMvc.perform(put("/group/1/member/2/remove-admin").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("已取消管理员"));
        verify(groupManageService).removeAdmin(eq(1L), eq(1L), eq(2L));
    }

    @Test
    void removeMember_shouldSucceed() throws Exception {
        mockMvc.perform(delete("/group/1/member/2").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("已移除成员"));
        verify(groupManageService).removeMember(eq(1L), eq(1L), eq(2L));
    }

    @Test
    void muteMember_shouldSucceed() throws Exception {
        mockMvc.perform(put("/group/1/member/2/mute").requestAttr("userId", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("minutes", 30))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("已禁言"));
        verify(groupManageService).muteMember(eq(1L), eq(1L), eq(2L), eq(30));
    }

    @Test
    void muteMember_withDefaultMinutes_shouldUse60() throws Exception {
        mockMvc.perform(put("/group/1/member/2/mute").requestAttr("userId", 1L)
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
        verify(groupManageService).muteMember(eq(1L), eq(1L), eq(2L), eq(60));
    }

    @Test
    void unmuteMember_shouldSucceed() throws Exception {
        mockMvc.perform(put("/group/1/member/2/unmute").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("已取消禁言"));
        verify(groupManageService).unmuteMember(eq(1L), eq(1L), eq(2L));
    }

    @Test
    void batchMute_shouldSucceed() throws Exception {
        mockMvc.perform(put("/group/1/members/batch-mute").requestAttr("userId", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("memberIds", List.of(2, 3), "minutes", 60))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("已批量禁言"));
        verify(groupManageService).batchMute(eq(1L), eq(1L), argThat(ids -> ids.size() == 2), eq(60));
    }
}
