package com.chat.chat_backend.modules.impression.controller;

import com.chat.chat_backend.modules.impression.service.ImpressionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class ImpressionControllerTest {

    @Mock
    private ImpressionService impressionService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ImpressionController(impressionService)).build();
    }

    @Test
    void addImpression_shouldSucceed() throws Exception {
        mockMvc.perform(post("/impression")
                .requestAttr("userId", 1L)
                .contentType("application/json")
                .content("{\"toUserId\":2,\"content\":\"很好的人\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("评价成功"));

        verify(impressionService).addImpression(eq(1L), any());
    }

    @Test
    void getImpressionsToMe_shouldReturnList() throws Exception {
        when(impressionService.getImpressionsToMe(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/impression/to-me").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(impressionService).getImpressionsToMe(1L);
    }

    @Test
    void getImpressionsByMe_shouldReturnList() throws Exception {
        when(impressionService.getImpressionsByMe(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/impression/by-me").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(impressionService).getImpressionsByMe(1L);
    }

    @Test
    void deleteImpression_shouldSucceed() throws Exception {
        mockMvc.perform(delete("/impression/3").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(impressionService).deleteImpression(1L, 3L);
    }
}
