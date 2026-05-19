package com.chat.chat_backend.modules.message.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.chat_backend.common.constant.RedisConstants;
import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.common.result.ResultCode;
import com.chat.chat_backend.common.utils.RedisHashUtil;
import com.chat.chat_backend.common.utils.RedisUtil;
import com.chat.chat_backend.modules.group.dto.response.UnreadGroupVO;
import com.chat.chat_backend.modules.message.dto.response.MessageVO;
import com.chat.chat_backend.modules.message.dto.response.UnreadCountVO;
import com.chat.chat_backend.modules.message.dto.response.UnreadMessageDetailVO;
import com.chat.chat_backend.modules.message.mapper.MessageMapper;
import com.chat.chat_backend.modules.message.service.MessageService;
import com.chat.chat_backend.modules.notification.mapper.SystemNotificationMapper;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class MessageOperateServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final SystemNotificationMapper systemNotificationMapper;
    private final RedisUtil redisUtil;
    private final RedisHashUtil redisHashUtil;
    private final MessageQueryServiceImpl messageQueryService;

    @Override
    public void markAsRead(Long userId, Long friendId) {
        messageMapper.markAsRead(userId, friendId);
        String unreadKey = RedisConstants.UNREAD_COUNT + userId;
        redisHashUtil.hashDelete(unreadKey, String.valueOf(friendId));
        log.info("标记已读: userId={}, friendId={}", userId, friendId);
    }

    @Override
    public UnreadCountVO getUnreadCount(Long userId) {
        Integer msgTotal = messageMapper.countUnreadTotal(userId);
        Integer notifTotal = systemNotificationMapper.countUnreadByUserId(userId);
        int total = msgTotal + notifTotal;

        List<UnreadGroupVO> groups = messageMapper.groupUnreadByFriend(userId);

        List<UnreadCountVO.UnreadDetail> details = new ArrayList<>();
        for (UnreadGroupVO group : groups) {
            if (group.getFromUserId() == null) continue;
            User friend = userMapper.selectById(group.getFromUserId());
            if (friend != null) {
                details.add(UnreadCountVO.UnreadDetail.builder()
                        .friendId(group.getFromUserId())
                        .friendNickname(friend.getNickname())
                        .friendAvatar(friend.getAvatar())
                        .unreadCount(group.getCount())
                        .build());
            }
        }

        List<UnreadMessageDetailVO> unreadMessages = messageMapper.findUnreadMessages(userId);
        List<UnreadCountVO.UnreadMessage> messages = new ArrayList<>();
        for (UnreadMessageDetailVO msg : unreadMessages) {
            String content = msg.getContent();
            if (content.length() > 50) {
                content = content.substring(0, 50) + "...";
            }
            messages.add(UnreadCountVO.UnreadMessage.builder()
                    .id(msg.getId())
                    .fromUserId(msg.getFromUserId())
                    .fromUserNickname(msg.getFromUserNickname())
                    .fromUserAvatar(msg.getFromUserAvatar())
                    .messageType(msg.getMessageType())
                    .content(content)
                    .sendTime(msg.getSendTime())
                    .build());
        }

        log.debug("获取未读统计: userId={}, total={}", userId, total);
        return UnreadCountVO.builder()
                .total(total)
                .details(details)
                .messages(messages)
                .build();
    }

    @Override
    public void recallMessage(Long userId, Long messageId) {
        int updated = messageMapper.recallMessage(messageId, userId);
        if (updated == 0) {
            log.warn("撤回消息失败（超时或无权）: userId={}, messageId={}", userId, messageId);
            throw new BusinessException(ResultCode.MESSAGE_RECALL_TIMEOUT);
        }
        log.info("撤回消息成功: userId={}, messageId={}", userId, messageId);
    }

    @Override
    public Page<MessageVO> getChatHistory(Long userId, Long friendId, Integer page, Integer size) {
        return messageQueryService.getChatHistory(userId, friendId, page, size);
    }

    @Override
    public List<MessageVO> downloadChatHistory(Long userId, Long friendId, Integer limit) {
        return messageQueryService.downloadChatHistory(userId, friendId, limit);
    }
}
