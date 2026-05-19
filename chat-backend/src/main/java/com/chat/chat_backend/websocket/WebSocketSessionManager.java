package com.chat.chat_backend.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket会话管理器，维护userId到WebSocketSession的映射
 * 提供线程安全的增删查操作和消息发送能力
 * @author chat-backend
 * @since 2026-05-12
 */
@Slf4j
@Component
public class WebSocketSessionManager {

    private final Map<Long, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    public void add(Long userId, WebSocketSession session) {
        if (userId != null && session != null) {
            sessionMap.put(userId, session);
        }
    }

    public void remove(Long userId) {
        if (userId != null) {
            sessionMap.remove(userId);
        }
    }

    public WebSocketSession get(Long userId) {
        return userId == null ? null : sessionMap.get(userId);
    }

    public boolean isOnline(Long userId) {
        return userId != null && sessionMap.containsKey(userId);
    }

    public int getOnlineCount() {
        return sessionMap.size();
    }

    public Map<Long, WebSocketSession> getAllSessions() {
        return sessionMap;
    }

    public void sendMessage(Long userId, String message) {
        WebSocketSession session = get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                log.debug("消息已发送给用户 {}", userId);
            } catch (IOException e) {
                log.error("发送消息给用户 {} 失败: {}", userId, e.getMessage());
            }
        } else {
            log.debug("用户 {} 不在线或会话已关闭", userId);
        }
    }
}
