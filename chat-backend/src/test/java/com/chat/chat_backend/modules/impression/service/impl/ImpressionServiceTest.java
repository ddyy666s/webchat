package com.chat.chat_backend.modules.impression.service.impl;

import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.modules.impression.dto.request.AddImpressionRequest;
import com.chat.chat_backend.modules.impression.entity.Impression;
import com.chat.chat_backend.modules.impression.mapper.ImpressionMapper;
import com.chat.chat_backend.modules.message.entity.Message;
import com.chat.chat_backend.modules.message.mapper.MessageMapper;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import com.chat.chat_backend.websocket.WebSocketSessionManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImpressionServiceTest {

    @Mock
    private ImpressionMapper impressionMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private WebSocketSessionManager sessionManager;
    @InjectMocks
    private ImpressionServiceImpl impressionService;

    @Test
    void addImpression_shouldCreateImpressionAndSystemMessage() {
        AddImpressionRequest request = new AddImpressionRequest();
        request.setToUserId(2L);
        request.setContent("很好的人");

        User fromUser = new User();
        fromUser.setId(1L);
        fromUser.setNickname("user1");
        User toUser = new User();
        toUser.setId(2L);
        toUser.setNickname("user2");

        when(userMapper.selectById(1L)).thenReturn(fromUser);
        when(userMapper.selectById(2L)).thenReturn(toUser);
        when(sessionManager.isOnline(2L)).thenReturn(false);

        impressionService.addImpression(1L, request);

        verify(impressionMapper).insert(any(Impression.class));
        verify(messageMapper).insert(any(Message.class));
    }

    @Test
    void addImpression_shouldThrowWhenContentTooLong() {
        AddImpressionRequest request = new AddImpressionRequest();
        request.setToUserId(2L);
        request.setContent("a".repeat(101));

        when(userMapper.selectById(2L)).thenReturn(new User());

        assertThrows(BusinessException.class, () -> impressionService.addImpression(1L, request));
    }

    @Test
    void addImpression_shouldThrowWhenSelfImpression() {
        AddImpressionRequest request = new AddImpressionRequest();
        request.setToUserId(1L);
        request.setContent("很好");

        assertThrows(BusinessException.class, () -> impressionService.addImpression(1L, request));
    }

    @Test
    void deleteImpression_shouldSoftDelete() {
        Impression impression = new Impression();
        impression.setId(1L);
        impression.setFromUserId(1L);
        impression.setIsDelete(0);

        when(impressionMapper.selectById(1L)).thenReturn(impression);

        impressionService.deleteImpression(1L, 1L);

        assertEquals(1, impression.getIsDelete());
        verify(impressionMapper).updateById(impression);
    }

    @Test
    void deleteImpression_shouldThrowWhenNotOwner() {
        Impression impression = new Impression();
        impression.setId(1L);
        impression.setFromUserId(2L);

        when(impressionMapper.selectById(1L)).thenReturn(impression);

        assertThrows(BusinessException.class, () -> impressionService.deleteImpression(1L, 1L));
        verify(impressionMapper, never()).updateById(any(Impression.class));
    }
}
