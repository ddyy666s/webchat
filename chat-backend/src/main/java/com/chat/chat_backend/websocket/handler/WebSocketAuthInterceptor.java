package com.chat.chat_backend.websocket.handler;

import com.chat.chat_backend.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import java.net.URI;

/**
 * WebSocket连接认证拦截器，从查询字符串中提取并验证JWT令牌
 * @author chat-backend
 * @since 2026-05-12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor {

    private final JwtUtil jwtUtil;

    public Long authenticate(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            log.warn("URI为空");
            return null;
        }
        String query = uri.getQuery();
        if (query == null || !query.contains("token=")) {
            log.warn("Query中无token");
            return null;
        }
        String token = query.split("token=")[1];
        if (token.contains("&")) {
            token = token.split("&")[0];
        }
        if (jwtUtil.validateToken(token)) {
            return jwtUtil.getUserIdFromToken(token);
        }
        log.warn("Token验证失败");
        return null;
    }
}
