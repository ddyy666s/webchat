package com.chat.chat_backend.modules.message.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.chat_backend.modules.message.dto.response.MessageVO;
import com.chat.chat_backend.modules.message.entity.Message;
import com.chat.chat_backend.modules.message.mapper.MessageMapper;
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
class MessageQueryServiceTest {

    @Mock
    private MessageMapper messageMapper;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private MessageQueryServiceImpl messageQueryService;

    @Test
    void getChatHistory_shouldReturnPageWithVoiceDuration() {
        Message voiceMsg = new Message();
        voiceMsg.setId(1L);
        voiceMsg.setFromUserId(1L);
        voiceMsg.setToUserId(2L);
        voiceMsg.setMessageType(4);
        voiceMsg.setContent("http://voice.url|30");
        voiceMsg.setIsRead(0);
        voiceMsg.setSendTime(LocalDateTime.now());

        Message textMsg = new Message();
        textMsg.setId(2L);
        textMsg.setFromUserId(1L);
        textMsg.setToUserId(2L);
        textMsg.setMessageType(1);
        textMsg.setContent("hello");
        textMsg.setIsRead(0);
        textMsg.setSendTime(LocalDateTime.now());

        User user = new User();
        user.setId(1L);
        user.setNickname("me");
        user.setAvatar("avatar");
        User friend = new User();
        friend.setId(2L);
        friend.setNickname("friend");

        when(messageMapper.findChatHistory(1L, 2L, 0, 20)).thenReturn(List.of(voiceMsg, textMsg));
        when(messageMapper.countChatHistory(1L, 2L)).thenReturn(2L);
        when(userMapper.selectById(1L)).thenReturn(user);
        when(userMapper.selectById(2L)).thenReturn(friend);

        Page<MessageVO> result = messageQueryService.getChatHistory(1L, 2L, 1, 20);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        MessageVO voiceVo = result.getRecords().get(0);
        assertEquals("http://voice.url", voiceVo.getContent());
        assertEquals(Integer.valueOf(30), voiceVo.getDuration());
        MessageVO textVo = result.getRecords().get(1);
        assertEquals("hello", textVo.getContent());
        assertNull(textVo.getDuration());
    }

    @Test
    void downloadChatHistory_shouldReturnLimitedMessages() {
        Message msg = new Message();
        msg.setId(1L);
        msg.setFromUserId(1L);
        msg.setContent("test content");
        msg.setMessageType(1);
        msg.setSendTime(LocalDateTime.now());

        User fromUser = new User();
        fromUser.setId(1L);
        fromUser.setNickname("me");

        when(messageMapper.findChatHistory(1L, 2L, 0, 100)).thenReturn(List.of(msg));
        when(userMapper.selectById(1L)).thenReturn(fromUser);

        List<MessageVO> result = messageQueryService.downloadChatHistory(1L, 2L, 100);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test content", result.get(0).getContent());
    }
}
