package com.chat.chat_backend.modules.emoji.service.impl;

import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.common.utils.OssUtil;
import com.chat.chat_backend.modules.emoji.dto.response.EmojiVO;
import com.chat.chat_backend.modules.emoji.entity.Emoji;
import com.chat.chat_backend.modules.emoji.mapper.EmojiMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmojiServiceTest {

    @Mock
    private EmojiMapper emojiMapper;
    @Mock
    private OssUtil ossUtil;
    @InjectMocks
    private EmojiServiceImpl emojiService;

    @Test
    void getSystemEmojis_shouldReturnList() {
        Emoji emoji = new Emoji();
        emoji.setId(1L);
        emoji.setName("smile");
        emoji.setUrl("http://url/smile.png");
        emoji.setCategory("default");
        emoji.setIsSystem(1);
        emoji.setCreatedAt(LocalDateTime.now());

        when(emojiMapper.findSystemEmojis()).thenReturn(List.of(emoji));

        List<EmojiVO> result = emojiService.getSystemEmojis();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("smile", result.get(0).getName());
        assertTrue(result.get(0).getIsSystem());
    }

    @Test
    void uploadEmoji_shouldUploadAndInsert() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "emoji.png", "image/png", "img".getBytes());
        when(ossUtil.uploadFile(file, "emoji/")).thenReturn("http://url/emoji.png");
        when(emojiMapper.insert(any(Emoji.class))).thenReturn(1);

        EmojiVO result = emojiService.uploadEmoji(1L, "myemoji", file, "custom");

        assertNotNull(result);
        assertEquals("myemoji", result.getName());
        assertEquals("http://url/emoji.png", result.getUrl());
        verify(emojiMapper).insert(any(Emoji.class));
    }

    @Test
    void deleteEmoji_shouldThrowWhenNotOwner() {
        Emoji emoji = new Emoji();
        emoji.setId(1L);
        emoji.setUserId(2L);
        emoji.setName("smile");

        when(emojiMapper.selectById(1L)).thenReturn(emoji);

        assertThrows(BusinessException.class, () -> emojiService.deleteEmoji(1L, 1L));
        verify(emojiMapper, never()).deleteById(anyLong());
    }

    @Test
    void deleteEmoji_shouldSucceedWhenOwner() {
        Emoji emoji = new Emoji();
        emoji.setId(1L);
        emoji.setUserId(1L);
        emoji.setName("smile");

        when(emojiMapper.selectById(1L)).thenReturn(emoji);

        emojiService.deleteEmoji(1L, 1L);
        verify(emojiMapper).deleteById(1L);
    }
}
