package com.chat.chat_backend.modules.health.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.util.Map;

/**
 * 健康检查控制器
 * @author chat-backend
 * @since 2026-05-12
 */
@RestController
@Tag(name = "Health", description = "健康检查接口")
public class HealthController {

    @GetMapping("/api/health")
    @Operation(summary = "健康检查")
    public Map<String, Object> health() {
        return Map.of("status", "UP", "timestamp", Instant.now().toString());
    }
}
