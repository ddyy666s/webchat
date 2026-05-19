package com.chat.chat_backend.modules.emoji.controller;

import com.chat.chat_backend.modules.emoji.dto.response.EmojiVO;
import com.chat.chat_backend.modules.emoji.service.EmojiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmojiControllerTest {

    @Mock
    private EmojiService emojiService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new EmojiController(emojiService)).build();
    }

    @Test
    void getSystemEmojis_shouldReturnList() throws Exception {
        when(emojiService.getSystemEmojis()).thenReturn(List.of());

        mockMvc.perform(get("/emoji/system"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void getUserEmojis_shouldReturnList() throws Exception {
        when(emojiService.getUserEmojis(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/emoji/user").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(emojiService).getUserEmojis(1L);
    }

    @Test
    void uploadEmoji_shouldReturnEmoji() throws Exception {
        when(emojiService.uploadEmoji(anyLong(), anyString(), any(MultipartFile.class), anyString()))
                .thenReturn(EmojiVO.builder().id(1L).name("test").url("url").build());

        MockMultipartFile file = new MockMultipartFile("file", "emoji.png", "image/png", "img".getBytes());
        mockMvc.perform(multipart("/emoji/upload").file(file)
                .param("name", "test").param("category", "custom")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteEmoji_shouldSucceed() throws Exception {
        mockMvc.perform(delete("/emoji/5").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(emojiService).deleteEmoji(1L, 5L);
    }
}
