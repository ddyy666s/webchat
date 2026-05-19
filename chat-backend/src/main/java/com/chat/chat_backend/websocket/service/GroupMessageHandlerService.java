package com.chat.chat_backend.websocket.service;

import cn.hutool.json.JSONUtil;
import com.chat.chat_backend.modules.group.mapper.GroupMemberMapper;
import com.chat.chat_backend.modules.group.mapper.GroupMessageMapper;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import com.chat.chat_backend.modules.group.entity.GroupMember;
import com.chat.chat_backend.modules.group.entity.GroupMessage;
import com.chat.chat_backend.modules.group.service.GroupMuteService;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 群聊消息处理与投递服务
 * 验证群成员身份和禁言状态，持久化消息，更新未读计数，推送给在线群成员
 * @author chat-backend
 * @since 2026-05-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupMessageHandlerService {

    private final GroupMemberMapper groupMemberMapper;
    private final GroupMessageMapper groupMessageMapper;
    private final UserMapper userMapper;
    private final WebSocketSessionManager sessionManager;
    private final GroupMuteService groupMuteService;

    public void sendAndNotify(Long fromUserId, Long groupId, String content, Integer messageType, Integer duration) {
        List<GroupMember> members = groupMemberMapper.findByGroupId(groupId);
        boolean isMember = members.stream().anyMatch(m -> m.getUserId().equals(fromUserId));
        if (!isMember) {
            log.error("用户 {} 不是群 {} 的成员", fromUserId, groupId);
            return;
        }

        if (groupMuteService.isMuted(groupId, fromUserId)) {
            log.warn("用户 {} 在群 {} 中被禁言", fromUserId, groupId);
            return;
        }

        GroupMessage msg = new GroupMessage();
        msg.setGroupId(groupId);
        msg.setFromUserId(fromUserId);
        msg.setMessageType(messageType);
        msg.setContent(content);
        msg.setSendTime(LocalDateTime.now());
        groupMessageMapper.insert(msg);

        groupMemberMapper.incrementUnreadCount(groupId, fromUserId);

        User fromUser = userMapper.selectById(fromUserId);
        String fromUserNickname = fromUser != null ? fromUser.getNickname() : "未知用户";

        var response = JSONUtil.createObj()
                .set("type", "group_message")
                .set("messageId", msg.getId())
                .set("groupId", groupId)
                .set("fromUserId", fromUserId)
                .set("fromUserNickname", fromUserNickname)
                .set("content", content)
                .set("messageType", messageType)
                .set("duration", duration)
                .set("sendTime", msg.getSendTime().toString());

        String responseStr = response.toString();

        for (GroupMember member : members) {
            if (!member.getUserId().equals(fromUserId) && sessionManager.isOnline(member.getUserId())) {
                sessionManager.sendMessage(member.getUserId(), responseStr);
            }
        }

        log.debug("群聊消息已投递: from={}, groupId={}, type={}", fromUserId, groupId, messageType);
    }
}
