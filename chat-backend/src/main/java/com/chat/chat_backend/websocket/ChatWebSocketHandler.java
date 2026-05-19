package com.chat.chat_backend.websocket;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.chat.chat_backend.websocket.handler.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 主要WebSocket处理器，管理连接生命周期、消息分发和状态广播
 * 根据消息类型将消息路由到对应的子处理器
 * @author chat-backend
 * @since 2026-05-12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionManager sessionManager;
    private final WebSocketAuthInterceptor authInterceptor;
    private final MessageHandler messageHandler;
    private final GroupMessageHandler groupMessageHandler;
    private final CallSignalHandler callSignalHandler;
    private final StatusHandler statusHandler;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = authInterceptor.authenticate(session);
        if (userId == null) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }
        sessionManager.add(userId, session);
        session.getAttributes().put("userId", userId);
        statusHandler.userOnline(userId);
        statusHandler.broadcastStatus(userId, true);
        log.info("用户 {} 已连接WebSocket，当前在线人数: {}", userId, sessionManager.getOnlineCount());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId == null) return;

        String payload = message.getPayload();
        JSONObject json = JSONUtil.parseObj(payload);
        String type = json.getStr("type");

        switch (type) {
            case "message":
                messageHandler.handle(userId, json);
                break;
            case "group_message":
                groupMessageHandler.handle(userId, json);
                break;
            case "call":
                callSignalHandler.handle(userId, json);
                break;
            case "ping":
                session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
                break;
            default:
                log.warn("未知消息类型: {}", type);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessionManager.remove(userId);
            statusHandler.userOffline(userId);
            statusHandler.broadcastStatus(userId, false);
            log.info("用户 {} 已断开WebSocket", userId);
        }
    }
}
