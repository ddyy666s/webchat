package com.chat.chat_backend.modules.friend.service;

import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.modules.friend.dto.request.HandleFriendRequest;
import com.chat.chat_backend.modules.friend.dto.request.SendFriendRequest;
import com.chat.chat_backend.modules.friend.entity.Friend;
import com.chat.chat_backend.modules.friend.entity.FriendRequest;
import com.chat.chat_backend.modules.friend.mapper.FriendMapper;
import com.chat.chat_backend.modules.friend.mapper.FriendRequestMapper;
import com.chat.chat_backend.modules.friend.service.impl.FriendRequestServiceImpl;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import com.chat.chat_backend.websocket.WebSocketSessionManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendRequestServiceTest {

    @Mock private FriendMapper friendMapper;
    @Mock private FriendRequestMapper friendRequestMapper;
    @Mock private UserMapper userMapper;
    @Mock private WebSocketSessionManager sessionManager;
    @InjectMocks private FriendRequestServiceImpl friendRequestService;

    @Test
    void sendFriendRequest_shouldSucceed() {
        SendFriendRequest req = new SendFriendRequest();
        req.setToUserId(2L);
        when(userMapper.selectById(2L)).thenReturn(new User());
        when(friendMapper.findFriendRelation(anyLong(), anyLong())).thenReturn(null);
        when(friendRequestMapper.findPendingRequest(anyLong(), anyLong())).thenReturn(null);
        when(userMapper.selectById(1L)).thenReturn(new User());

        friendRequestService.sendFriendRequest(1L, req);
        verify(friendRequestMapper).insert(any(FriendRequest.class));
    }

    @Test
    void sendFriendRequest_selfAdd_shouldThrow() {
        SendFriendRequest req = new SendFriendRequest();
        req.setToUserId(1L);
        assertThrows(BusinessException.class, () -> friendRequestService.sendFriendRequest(1L, req));
    }

    @Test
    void sendFriendRequest_targetNotFound_shouldThrow() {
        SendFriendRequest req = new SendFriendRequest();
        req.setToUserId(2L);
        when(userMapper.selectById(2L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> friendRequestService.sendFriendRequest(1L, req));
    }

    @Test
    void sendFriendRequest_alreadyFriend_shouldThrow() {
        SendFriendRequest req = new SendFriendRequest();
        req.setToUserId(2L);
        when(userMapper.selectById(2L)).thenReturn(new User());
        when(friendMapper.findFriendRelation(anyLong(), anyLong())).thenReturn(new Friend());
        assertThrows(BusinessException.class, () -> friendRequestService.sendFriendRequest(1L, req));
    }

    @Test
    void sendFriendRequest_duplicateRequest_shouldThrow() {
        SendFriendRequest req = new SendFriendRequest();
        req.setToUserId(2L);
        when(userMapper.selectById(2L)).thenReturn(new User());
        when(friendMapper.findFriendRelation(anyLong(), anyLong())).thenReturn(null);
        when(friendRequestMapper.findPendingRequest(anyLong(), anyLong())).thenReturn(new FriendRequest());
        assertThrows(BusinessException.class, () -> friendRequestService.sendFriendRequest(1L, req));
    }

    @Test
    void handleFriendRequest_accept_shouldSucceed() {
        FriendRequest fr = new FriendRequest();
        fr.setToUserId(1L);
        fr.setFromUserId(2L);
        fr.setExpireTime(LocalDateTime.now().plusDays(1));
        when(friendRequestMapper.selectById(1L)).thenReturn(fr);
        HandleFriendRequest req = new HandleFriendRequest();
        req.setStatus(1);

        friendRequestService.handleFriendRequest(1L, 1L, req);
        assertEquals(1, fr.getStatus());
        verify(friendMapper, times(2)).insert(any(Friend.class));
        verify(friendRequestMapper).updateById(fr);
    }

    @Test
    void handleFriendRequest_reject_shouldSucceed() {
        FriendRequest fr = new FriendRequest();
        fr.setToUserId(1L);
        fr.setFromUserId(2L);
        fr.setExpireTime(LocalDateTime.now().plusDays(1));
        when(friendRequestMapper.selectById(1L)).thenReturn(fr);
        HandleFriendRequest req = new HandleFriendRequest();
        req.setStatus(2);

        friendRequestService.handleFriendRequest(1L, 1L, req);
        assertEquals(2, fr.getStatus());
        verify(friendMapper, never()).insert(any(Friend.class));
    }

    @Test
    void handleFriendRequest_expired_shouldThrow() {
        FriendRequest fr = new FriendRequest();
        fr.setToUserId(1L);
        fr.setExpireTime(LocalDateTime.now().minusDays(1));
        when(friendRequestMapper.selectById(1L)).thenReturn(fr);
        HandleFriendRequest req = new HandleFriendRequest();
        req.setStatus(1);

        assertThrows(BusinessException.class, () -> friendRequestService.handleFriendRequest(1L, 1L, req));
        assertEquals(3, fr.getStatus());
    }

    @Test
    void handleFriendRequest_wrongOwner_shouldThrow() {
        FriendRequest fr = new FriendRequest();
        fr.setToUserId(2L);
        fr.setExpireTime(LocalDateTime.now().plusDays(1));
        when(friendRequestMapper.selectById(1L)).thenReturn(fr);
        HandleFriendRequest req = new HandleFriendRequest();
        req.setStatus(1);

        assertThrows(BusinessException.class, () -> friendRequestService.handleFriendRequest(1L, 1L, req));
    }

    @Test
    void handleFriendRequest_notFound_shouldThrow() {
        when(friendRequestMapper.selectById(1L)).thenReturn(null);
        HandleFriendRequest req = new HandleFriendRequest();
        req.setStatus(1);

        assertThrows(BusinessException.class, () -> friendRequestService.handleFriendRequest(1L, 1L, req));
    }
}
