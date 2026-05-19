package com.chat.chat_backend.websocket.handler;

import cn.hutool.json.JSONObject;
import com.chat.chat_backend.websocket.service.MessageHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 私聊消息处理器，委托MessageHandlerService进行消息持久化和推送
 * @author chat-backend
 * @since 2026-05-12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandler {

    private final MessageHandlerService messageHandlerService;

    public void handle(Long fromUserId, JSONObject json) {
        Long toUserId = json.getLong("toUserId");
        String content = json.getStr("content");
        Integer messageType = json.getInt("messageType", 1);
        Integer duration = json.getInt("duration");

        log.info("单聊消息: from={}, to={}, type={}", fromUserId, toUserId, messageType);

        messageHandlerService.sendAndNotify(fromUserId, toUserId, content, messageType, duration);
    }
}
