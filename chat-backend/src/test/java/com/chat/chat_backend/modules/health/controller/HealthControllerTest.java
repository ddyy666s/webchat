package com.chat.chat_backend.modules.health.controller;

import com.chat.chat_backend.BaseControllerTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HealthControllerTest extends BaseControllerTest {

    @Test
    void health_shouldReturnUp() throws Exception {
        performGet("/api/health")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
