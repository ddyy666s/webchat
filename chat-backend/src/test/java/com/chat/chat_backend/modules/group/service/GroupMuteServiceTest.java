package com.chat.chat_backend.modules.group.service;

import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.modules.group.entity.GroupMember;
import com.chat.chat_backend.modules.group.mapper.GroupMemberMapper;
import com.chat.chat_backend.modules.group.service.impl.GroupMuteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupMuteServiceTest {

    @Mock private GroupMemberMapper groupMemberMapper;
    @InjectMocks private GroupMuteServiceImpl groupMuteService;

    @BeforeEach
    void setUp() throws Exception {
        Field field = GroupMuteServiceImpl.class.getDeclaredField("muteMap");
        field.setAccessible(true);
        ((Map<?, ?>) field.get(null)).clear();
    }

    @SuppressWarnings("unchecked")
    private Map<String, LocalDateTime> getMuteMap() throws Exception {
        Field field = GroupMuteServiceImpl.class.getDeclaredField("muteMap");
        field.setAccessible(true);
        return (Map<String, LocalDateTime>) field.get(null);
    }

    private GroupMember createMember(Long userId, Integer role) {
        GroupMember m = new GroupMember();
        m.setUserId(userId);
        m.setRole(role);
        return m;
    }

    @Test
    void isMuted_whenNotMuted_shouldReturnFalse() {
        assertFalse(groupMuteService.isMuted(1L, 2L));
    }

    @Test
    void muteMember_whenAdminMutesMember_shouldSucceed() {
        when(groupMemberMapper.selectOne(any())).thenReturn(createMember(1L, 1), createMember(2L, 0));
        groupMuteService.muteMember(1L, 1L, 2L, 30);
        assertTrue(groupMuteService.isMuted(1L, 2L));
    }

    @Test
    void muteMember_whenNotAdmin_shouldThrow() {
        when(groupMemberMapper.selectOne(any())).thenReturn(createMember(1L, 0));
        assertThrows(BusinessException.class, () -> groupMuteService.muteMember(1L, 1L, 2L, 30));
    }

    @Test
    void muteMember_whenTargetIsOwner_shouldThrow() {
        when(groupMemberMapper.selectOne(any())).thenReturn(createMember(1L, 2), createMember(2L, 2));
        assertThrows(BusinessException.class, () -> groupMuteService.muteMember(1L, 1L, 2L, 30));
    }

    @Test
    void muteMember_whenAdminMutesAdmin_shouldThrow() {
        when(groupMemberMapper.selectOne(any())).thenReturn(createMember(1L, 1), createMember(2L, 1));
        assertThrows(BusinessException.class, () -> groupMuteService.muteMember(1L, 1L, 2L, 30));
    }

    @Test
    void muteMember_whenTargetNotInGroup_shouldThrow() {
        when(groupMemberMapper.selectOne(argThat(q -> true)))
                .thenReturn(createMember(1L, 2))
                .thenReturn(null);
        assertThrows(BusinessException.class, () -> groupMuteService.muteMember(1L, 1L, 2L, 30));
    }

    @Test
    void unmuteMember_whenAdminUnmutes_shouldSucceed() throws Exception {
        Map<String, LocalDateTime> muteMap = getMuteMap();
        muteMap.put("1:2", LocalDateTime.now().plusMinutes(30));
        assertTrue(groupMuteService.isMuted(1L, 2L));

        when(groupMemberMapper.selectOne(any())).thenReturn(createMember(1L, 1), createMember(2L, 0));
        groupMuteService.unmuteMember(1L, 1L, 2L);
        assertFalse(groupMuteService.isMuted(1L, 2L));
    }

    @Test
    void unmuteMember_whenNotAdmin_shouldThrow() {
        when(groupMemberMapper.selectOne(any())).thenReturn(createMember(1L, 0));
        assertThrows(BusinessException.class, () -> groupMuteService.unmuteMember(1L, 1L, 2L));
    }

    @Test
    void batchMute_shouldMuteEligibleMembers() {
        when(groupMemberMapper.selectOne(any()))
                .thenReturn(createMember(1L, 2))
                .thenReturn(createMember(2L, 0))
                .thenReturn(createMember(3L, 2))
                .thenReturn(null)
                .thenReturn(createMember(5L, 1));

        groupMuteService.batchMute(1L, 1L, List.of(2L, 3L, 4L, 5L), 60);
        assertTrue(groupMuteService.isMuted(1L, 2L));
        assertFalse(groupMuteService.isMuted(1L, 3L));
        assertFalse(groupMuteService.isMuted(1L, 4L));
        assertTrue(groupMuteService.isMuted(1L, 5L));
    }

    @Test
    void muteMember_whenOperatorNotInGroup_shouldThrow() {
        when(groupMemberMapper.selectOne(any())).thenReturn(null);
        assertThrows(BusinessException.class, () -> groupMuteService.muteMember(1L, 1L, 2L, 30));
    }
}
