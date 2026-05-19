package com.chat.chat_backend.modules.group.service;

import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.modules.group.dto.request.CreateGroupRequest;
import com.chat.chat_backend.modules.group.dto.response.GroupVO;
import com.chat.chat_backend.modules.group.entity.Group;
import com.chat.chat_backend.modules.group.entity.GroupMember;
import com.chat.chat_backend.modules.group.mapper.GroupMapper;
import com.chat.chat_backend.modules.group.mapper.GroupMemberMapper;
import com.chat.chat_backend.modules.group.service.impl.GroupServiceHelper;
import com.chat.chat_backend.modules.group.service.impl.GroupServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock private GroupMapper groupMapper;
    @Mock private GroupMemberMapper groupMemberMapper;
    @Mock private GroupMuteService groupMuteService;
    @Mock private GroupServiceHelper helper;
    @InjectMocks private GroupServiceImpl groupService;

    @Test
    void createGroup_withoutMembers_shouldSucceed() {
        CreateGroupRequest req = new CreateGroupRequest(); req.setName("test");
        when(groupMapper.insert(any(Group.class))).thenAnswer(i -> { ((Group)i.getArgument(0)).setId(1L); return 1; });
        GroupVO vo = GroupVO.builder().id(1L).name("test").build();
        when(helper.toGroupVO(any())).thenReturn(vo);
        GroupVO result = groupService.createGroup(1L, req);
        assertEquals(1L, result.getId());
        verify(helper).insertMember(eq(1L), eq(1L), eq(2));
    }

    @Test
    void createGroup_withMembers_shouldSucceed() {
        CreateGroupRequest req = new CreateGroupRequest(); req.setName("test"); req.setMemberIds(List.of(2L, 3L));
        when(groupMapper.insert(any(Group.class))).thenAnswer(i -> { ((Group)i.getArgument(0)).setId(1L); return 1; });
        when(helper.toGroupVO(any())).thenReturn(GroupVO.builder().id(1L).build());
        groupService.createGroup(1L, req);
        verify(helper).insertMember(eq(1L), eq(2L), eq(0));
        verify(helper).insertMember(eq(1L), eq(3L), eq(0));
    }

    @Test
    void quitGroup_whenOwner_shouldThrow() {
        Group g = new Group(); g.setOwnerId(1L);
        when(helper.getGroupOrThrow(anyLong())).thenReturn(g);
        assertThrows(BusinessException.class, () -> groupService.quitGroup(1L, 1L));
    }

    @Test
    void quitGroup_whenNonOwner_shouldSucceed() {
        Group g = new Group(); g.setOwnerId(2L); g.setMemberCount(5);
        when(helper.getGroupOrThrow(anyLong())).thenReturn(g);
        groupService.quitGroup(1L, 1L);
        verify(groupMemberMapper).delete(any());
        verify(groupMapper).updateById(g);
        assertEquals(4, g.getMemberCount());
    }

    @Test
    void disbandGroup_whenOwner_shouldSucceed() {
        Group g = new Group(); g.setOwnerId(1L);
        when(helper.getGroupOrThrow(anyLong())).thenReturn(g);
        groupService.disbandGroup(1L, 1L);
        verify(groupMemberMapper).delete(any());
        verify(groupMapper).deleteById(1L);
    }

    @Test
    void disbandGroup_whenNonOwner_shouldThrow() {
        Group g = new Group(); g.setOwnerId(2L);
        when(helper.getGroupOrThrow(anyLong())).thenReturn(g);
        assertThrows(BusinessException.class, () -> groupService.disbandGroup(1L, 1L));
    }

    @Test
    void setAdmin_whenOwner_shouldSucceed() {
        GroupMember m = new GroupMember(); m.setRole(0);
        when(helper.findMember(anyLong(), eq(2L))).thenReturn(m);
        groupService.setAdmin(1L, 1L, 2L);
        assertEquals(1, m.getRole());
        verify(groupMemberMapper).updateById(m);
    }

    @Test
    void setAdmin_whenTargetIsOwner_shouldThrow() {
        GroupMember m = new GroupMember(); m.setRole(2);
        when(helper.findMember(anyLong(), eq(2L))).thenReturn(m);
        assertThrows(BusinessException.class, () -> groupService.setAdmin(1L, 1L, 2L));
    }

    @Test
    void removeAdmin_whenOwner_shouldSucceed() {
        GroupMember m = new GroupMember(); m.setRole(1);
        when(helper.findMember(anyLong(), eq(2L))).thenReturn(m);
        groupService.removeAdmin(1L, 1L, 2L);
        assertEquals(0, m.getRole());
        verify(groupMemberMapper).updateById(m);
    }

    @Test
    void removeAdmin_whenTargetNotAdmin_shouldThrow() {
        GroupMember m = new GroupMember(); m.setRole(0);
        when(helper.findMember(anyLong(), eq(2L))).thenReturn(m);
        assertThrows(BusinessException.class, () -> groupService.removeAdmin(1L, 1L, 2L));
    }

    @Test
    void removeMember_whenNotAdmin_shouldThrow() {
        Group g = new Group(); g.setOwnerId(3L);
        GroupMember op = new GroupMember(); op.setRole(0);
        when(helper.getGroupOrThrow(anyLong())).thenReturn(g);
        when(helper.findMember(eq(1L), eq(1L))).thenReturn(op);
        assertThrows(BusinessException.class, () -> groupService.removeMember(1L, 1L, 2L));
    }

    @Test
    void removeMember_whenOwnerRemovesMember_shouldSucceed() {
        Group g = new Group(); g.setMemberCount(5);
        GroupMember op = new GroupMember(); op.setRole(2);
        GroupMember t = new GroupMember(); t.setId(1L); t.setRole(0);
        when(helper.getGroupOrThrow(anyLong())).thenReturn(g);
        when(helper.findMember(eq(1L), eq(1L))).thenReturn(op);
        when(helper.findMember(eq(1L), eq(2L))).thenReturn(t);
        groupService.removeMember(1L, 1L, 2L);
        verify(groupMemberMapper).deleteById(1L);
        assertEquals(4, g.getMemberCount());
    }
}
