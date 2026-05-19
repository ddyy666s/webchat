package com.chat.chat_backend.websocket.handler;

import cn.hutool.json.JSONObject;
import com.chat.chat_backend.common.constant.RedisConstants;
import com.chat.chat_backend.common.utils.RedisUtil;
import com.chat.chat_backend.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 用户在线/离线状态处理器，在Redis中维护状态并向所有已连接用户广播
 * @author chat-backend
 * @since 2026-05-12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatusHandler {

    private final RedisUtil redisUtil;
    private final WebSocketSessionManager sessionManager;

    public void userOnline(Long userId) {
        redisUtil.addToSet(RedisConstants.ONLINE_USERS, String.valueOf(userId));
        redisUtil.expire(RedisConstants.ONLINE_USERS, RedisConstants.ONLINE_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    public void userOffline(Long userId) {
        redisUtil.removeFromSet(RedisConstants.ONLINE_USERS, String.valueOf(userId));
    }

    public void broadcastStatus(Long userId, boolean online) {
        JSONObject statusMsg = new JSONObject();
        statusMsg.set("type", "status");
        statusMsg.set("userId", userId);
        statusMsg.set("online", online);

        String jsonMsg = statusMsg.toString();

        for (WebSocketSession session : sessionManager.getAllSessions().values()) {
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(jsonMsg));
                } catch (IOException e) {
                    log.warn("广播状态失败: {}", e.getMessage());
                }
            }
        }
    }
}
