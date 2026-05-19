package com.chat.chat_backend.modules.group.controller;

import com.chat.chat_backend.modules.group.dto.request.CreateGroupRequest;
import com.chat.chat_backend.modules.group.dto.request.InviteMemberRequest;
import com.chat.chat_backend.modules.group.dto.response.GroupMemberVO;
import com.chat.chat_backend.modules.group.dto.response.GroupVO;
import com.chat.chat_backend.modules.group.entity.GroupMember;
import com.chat.chat_backend.modules.group.mapper.GroupMemberMapper;
import com.chat.chat_backend.modules.group.service.GroupMuteService;
import com.chat.chat_backend.modules.group.service.GroupService;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
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
class GroupControllerTest {

    @Mock private GroupService groupService;
    @Mock private GroupMuteService groupMuteService;
    @Mock private GroupMemberMapper groupMemberMapper;
    @Mock private UserMapper userMapper;
    @InjectMocks private GroupController groupController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
    }

    @Test
    void createGroup_shouldReturnGroup() throws Exception {
        GroupVO vo = GroupVO.builder().id(1L).name("testGroup").build();
        when(groupService.createGroup(anyLong(), any())).thenReturn(vo);
        CreateGroupRequest req = new CreateGroupRequest();
        req.setName("testGroup");

        mockMvc.perform(post("/group").requestAttr("userId", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void getUserGroups_shouldReturnList() throws Exception {
        GroupVO vo = GroupVO.builder().id(1L).name("g").build();
        when(groupService.getUserGroups(anyLong())).thenReturn(List.of(vo));

        mockMvc.perform(get("/group/list").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }

    @Test
    void getGroupDetail_shouldReturnGroup() throws Exception {
        GroupVO vo = GroupVO.builder().id(1L).name("g").build();
        when(groupService.getGroupDetail(anyLong(), anyLong())).thenReturn(vo);

        mockMvc.perform(get("/group/1").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void inviteMember_shouldSucceed() throws Exception {
        InviteMemberRequest req = new InviteMemberRequest();
        req.setGroupId(1L);
        req.setUserId(2L);

        mockMvc.perform(post("/group/invite").requestAttr("userId", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("邀请成功"));
    }

    @Test
    void quitGroup_shouldSucceed() throws Exception {
        mockMvc.perform(delete("/group/1/quit").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("已退出群聊"));
    }

    @Test
    void disbandGroup_shouldSucceed() throws Exception {
        mockMvc.perform(delete("/group/1/disband").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("群聊已解散"));
    }

    @Test
    void getGroupMembers_shouldReturnList() throws Exception {
        GroupMember member = new GroupMember();
        member.setUserId(2L);
        member.setRole(0);
        when(groupMemberMapper.findByGroupId(anyLong())).thenReturn(List.of(member));
        User user = new User();
        user.setNickname("test");
        when(userMapper.selectById(2L)).thenReturn(user);
        when(groupMuteService.isMuted(anyLong(), anyLong())).thenReturn(false);

        mockMvc.perform(get("/group/1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userId").value(2L));
    }

    @Test
    void clearUnreadCount_shouldSucceed() throws Exception {
        mockMvc.perform(put("/group/1/read").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("已清除未读"));
    }
}
