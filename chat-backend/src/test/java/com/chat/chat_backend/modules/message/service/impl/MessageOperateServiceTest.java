package com.chat.chat_backend.modules.message.service.impl;

import com.chat.chat_backend.common.constant.RedisConstants;
import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.common.utils.RedisHashUtil;
import com.chat.chat_backend.common.utils.RedisUtil;
import com.chat.chat_backend.modules.group.dto.response.UnreadGroupVO;
import com.chat.chat_backend.modules.message.dto.response.UnreadCountVO;
import com.chat.chat_backend.modules.message.dto.response.UnreadMessageDetailVO;
import com.chat.chat_backend.modules.message.mapper.MessageMapper;
import com.chat.chat_backend.modules.notification.mapper.SystemNotificationMapper;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageOperateServiceTest {

    @Mock
    private MessageMapper messageMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private SystemNotificationMapper systemNotificationMapper;
    @Mock
    private RedisUtil redisUtil;
    @Mock
    private RedisHashUtil redisHashUtil;
    @Mock
    private MessageQueryServiceImpl messageQueryService;
    @InjectMocks
    private MessageOperateServiceImpl messageOperateService;

    @Test
    void markAsRead_shouldCallMapperAndDeleteRedisKey() {
        messageOperateService.markAsRead(1L, 2L);

        verify(messageMapper).markAsRead(1L, 2L);
        verify(redisHashUtil).hashDelete(RedisConstants.UNREAD_COUNT + 1L, String.valueOf(2L));
    }

    @Test
    void recallMessage_shouldSucceed() {
        when(messageMapper.recallMessage(100L, 1L)).thenReturn(1);

        messageOperateService.recallMessage(1L, 100L);

        verify(messageMapper).recallMessage(100L, 1L);
    }

    @Test
    void recallMessage_shouldThrowWhenNotOwner() {
        when(messageMapper.recallMessage(100L, 1L)).thenReturn(0);

        assertThrows(BusinessException.class, () -> messageOperateService.recallMessage(1L, 100L));
    }

    @Test
    void getUnreadCount_shouldReturnAggregatedResult() {
        when(messageMapper.countUnreadTotal(1L)).thenReturn(3);
        when(systemNotificationMapper.countUnreadByUserId(1L)).thenReturn(2);

        UnreadGroupVO group = new UnreadGroupVO();
        group.setFromUserId(2L);
        group.setCount(3);
        when(messageMapper.groupUnreadByFriend(1L)).thenReturn(List.of(group));

        User friend = new User();
        friend.setId(2L);
        friend.setNickname("friend");
        friend.setAvatar("avatar");
        when(userMapper.selectById(2L)).thenReturn(friend);

        UnreadMessageDetailVO msg = new UnreadMessageDetailVO();
        msg.setId(1L);
        msg.setFromUserId(2L);
        msg.setFromUserNickname("friend");
        msg.setContent("hello");
        msg.setMessageType(1);
        msg.setSendTime(LocalDateTime.now());
        when(messageMapper.findUnreadMessages(1L)).thenReturn(List.of(msg));

        UnreadCountVO result = messageOperateService.getUnreadCount(1L);

        assertNotNull(result);
        assertEquals(5, result.getTotal());
        assertEquals(1, result.getDetails().size());
        assertEquals(2L, result.getDetails().get(0).getFriendId());
        assertEquals(1, result.getMessages().size());
        assertEquals("hello", result.getMessages().get(0).getContent());
    }

    @Test
    void getChatHistory_shouldDelegate() {
        messageOperateService.getChatHistory(1L, 2L, 1, 20);
        verify(messageQueryService).getChatHistory(1L, 2L, 1, 20);
    }

    @Test
    void downloadChatHistory_shouldDelegate() {
        messageOperateService.downloadChatHistory(1L, 2L, 100);
        verify(messageQueryService).downloadChatHistory(1L, 2L, 100);
    }
}
