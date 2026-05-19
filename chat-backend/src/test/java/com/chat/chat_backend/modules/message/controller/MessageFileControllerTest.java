package com.chat.chat_backend.modules.message.controller;

import com.chat.chat_backend.common.utils.OssUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MessageFileControllerTest {

    @Mock
    private OssUtil ossUtil;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MessageFileController(ossUtil)).build();
    }

    @Test
    void uploadImage_shouldReturnUrl() throws Exception {
        when(ossUtil.uploadFile(any(MultipartFile.class), anyString())).thenReturn("http://oss.url/image.jpg");

        MockMultipartFile file = new MockMultipartFile("file", "img.jpg", "image/jpeg", "data".getBytes());
        mockMvc.perform(multipart("/message/upload/image").file(file).requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("http://oss.url/image.jpg"));

        verify(ossUtil).uploadFile(any(MultipartFile.class), eq("chat/images/"));
    }

    @Test
    void uploadVoice_shouldReturnUrl() throws Exception {
        when(ossUtil.uploadFile(any(MultipartFile.class), anyString())).thenReturn("http://oss.url/voice.amr");

        MockMultipartFile file = new MockMultipartFile("file", "voice.amr", "audio/amr", "audio".getBytes());
        mockMvc.perform(multipart("/message/upload/voice").file(file).requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("http://oss.url/voice.amr"));

        verify(ossUtil).uploadFile(any(MultipartFile.class), eq("chat/voice/"));
    }

    @Test
    void proxyAudio_shouldReturn500WhenInvalidUrl() throws Exception {
        mockMvc.perform(get("/message/proxy-audio").param("url", "invalid"))
                .andExpect(status().isInternalServerError());
    }
}
