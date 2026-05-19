package com.chat.chat_backend.websocket.service;

import cn.hutool.json.JSONUtil;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * WebRTC通话信令转发服务，通过WebSocket在端对端之间转发offer/answer/ICE候选消息
 * @author chat-backend
 * @since 2026-05-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CallSignalHandlerService {

    private final UserMapper userMapper;
    private final WebSocketSessionManager sessionManager;

    public void forward(Long fromUserId, Long toUserId, String callType, String action,
                        String sdp, String candidate, String sdpMid, Integer sdpMLineIndex) {

        User fromUser = userMapper.selectById(fromUserId);
        String fromUserNickname = fromUser != null ? fromUser.getNickname() : "用户";

        var response = JSONUtil.createObj()
                .set("type", "call")
                .set("action", action)
                .set("fromUserId", fromUserId)
                .set("fromUserNickname", fromUserNickname)
                .set("callType", callType);

        if ("offer".equals(action) || "answer".equals(action)) {
            response.set("sdp", sdp);
        } else if ("ice-candidate".equals(action)) {
            response.set("candidate", candidate);
            response.set("sdpMid", sdpMid);
            response.set("sdpMLineIndex", sdpMLineIndex);
        }

        sessionManager.sendMessage(toUserId, response.toString());
        log.info("转发通话信令: {} -> {}, action={}", fromUserId, toUserId, action);
    }
}
