package com.chat.chat_backend.modules.friend.controller;

import com.chat.chat_backend.modules.friend.dto.request.HandleFriendRequest;
import com.chat.chat_backend.modules.friend.dto.request.MoveFriendGroupRequest;
import com.chat.chat_backend.modules.friend.dto.request.SendFriendRequest;
import com.chat.chat_backend.modules.friend.dto.response.FriendGroupVO;
import com.chat.chat_backend.modules.friend.dto.response.FriendRequestVO;
import com.chat.chat_backend.modules.friend.dto.response.FriendVO;
import com.chat.chat_backend.modules.friend.service.FriendRelationService;
import com.chat.chat_backend.modules.friend.service.FriendRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
class FriendControllerTest {

    @Mock private FriendRelationService friendRelationService;
    @Mock private FriendRequestService friendRequestService;
    @InjectMocks private FriendController friendController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(friendController).build();
    }

    @Test
    void searchUsers_shouldReturnList() throws Exception {
        FriendVO vo = FriendVO.builder().userId(2L).nickname("test").build();
        when(friendRelationService.searchUsers(anyLong(), anyString())).thenReturn(List.of(vo));

        mockMvc.perform(get("/friend/search").param("keyword", "test")
                        .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userId").value(2L));
    }

    @Test
    void sendFriendRequest_shouldSucceed() throws Exception {
        SendFriendRequest req = new SendFriendRequest();
        req.setToUserId(2L);

        mockMvc.perform(post("/friend/request").requestAttr("userId", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("好友申请已发送"));
    }

    @Test
    void getFriendRequests_shouldReturnList() throws Exception {
        FriendRequestVO vo = FriendRequestVO.builder().id(1L).fromUserId(2L).build();
        when(friendRequestService.getFriendRequests(anyLong())).thenReturn(List.of(vo));

        mockMvc.perform(get("/friend/requests").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void handleFriendRequest_shouldSucceed() throws Exception {
        HandleFriendRequest req = new HandleFriendRequest();
        req.setStatus(1);

        mockMvc.perform(put("/friend/request/1").requestAttr("userId", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("已同意"));
    }

    @Test
    void getFriendList_shouldReturnList() throws Exception {
        FriendGroupVO vo = FriendGroupVO.builder().groupName("我的好友").friends(List.of()).build();
        when(friendRelationService.getFriendList(anyLong())).thenReturn(List.of(vo));

        mockMvc.perform(get("/friend/list").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].groupName").value("我的好友"));
    }

    @Test
    void deleteFriend_shouldSucceed() throws Exception {
        mockMvc.perform(delete("/friend/2").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    void moveFriendGroup_shouldSucceed() throws Exception {
        MoveFriendGroupRequest req = new MoveFriendGroupRequest();
        req.setGroupName("同事");

        mockMvc.perform(put("/friend/2/group").requestAttr("userId", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("移动成功"));
    }

    @Test
    void updateFriendRemark_shouldSucceed() throws Exception {
        mockMvc.perform(put("/friend/2/remark").param("remark", "newRemark")
                        .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("修改成功"));
    }
}
