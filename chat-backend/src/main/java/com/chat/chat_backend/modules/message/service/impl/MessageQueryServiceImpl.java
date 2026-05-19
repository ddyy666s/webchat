package com.chat.chat_backend.modules.message.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.chat_backend.common.constant.MessageConstants;
import com.chat.chat_backend.modules.message.dto.response.MessageVO;
import com.chat.chat_backend.modules.message.dto.response.UnreadCountVO;
import com.chat.chat_backend.modules.message.entity.Message;
import com.chat.chat_backend.modules.message.mapper.MessageMapper;
import com.chat.chat_backend.modules.message.service.MessageService;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageQueryServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @Override
    public Page<MessageVO> getChatHistory(Long userId, Long friendId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<Message> messages = messageMapper.findChatHistory(userId, friendId, offset, size);
        Long total = messageMapper.countChatHistory(userId, friendId);

        User friend = userMapper.selectById(friendId);
        User currentUser = userMapper.selectById(userId);

        List<MessageVO> voList = messages.stream()
                .map(msg -> {
                    User fromUser = userMapper.selectById(msg.getFromUserId());
                    String fromUserNickname = fromUser != null ? fromUser.getNickname() : "未知用户";
                    String fromUserAvatar = fromUser != null ? fromUser.getAvatar() : null;

                    String content = msg.getContent();
                    Integer duration = null;

                    if (msg.getMessageType() == 4 && content != null && content.contains("|")) {
                        String[] parts = content.split("\\|");
                        content = parts[0];
                        if (parts.length > 1) {
                            try {
                                duration = Integer.parseInt(parts[1]);
                            } catch (NumberFormatException e) {
                                duration = null;
                            }
                        }
                    }

                    return MessageVO.builder()
                            .id(msg.getId())
                            .fromUserId(msg.getFromUserId())
                            .fromUserNickname(fromUserNickname)
                            .fromUserAvatar(fromUserAvatar)
                            .toUserId(msg.getToUserId())
                            .toUserNickname(msg.getToUserId().equals(userId) ?
                                    currentUser.getNickname() : friend.getNickname())
                            .messageType(msg.getMessageType())
                            .content(content)
                            .duration(duration)
                            .isRead(msg.getIsRead() == 1)
                            .isRecalled(msg.getRecallTime() != null)
                            .sendTime(msg.getSendTime())
                            .build();
                })
                .collect(Collectors.toList());

        Page<MessageVO> pageResult = new Page<>(page, size);
        pageResult.setRecords(voList);
        pageResult.setTotal(total);
        log.debug("查询聊天记录: userId={}, friendId={}, page={}, size={}, total={}", userId, friendId, page, size, total);
        return pageResult;
    }

    @Override
    public List<MessageVO> downloadChatHistory(Long userId, Long friendId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = MessageConstants.DEFAULT_DOWNLOAD_SIZE;
        }
        if (limit > MessageConstants.MAX_DOWNLOAD_SIZE) {
            limit = MessageConstants.MAX_DOWNLOAD_SIZE;
        }

        List<Message> messages = messageMapper.findChatHistory(userId, friendId, 0, limit);

        List<MessageVO> result = messages.stream()
                .map(msg -> {
                    User fromUser = userMapper.selectById(msg.getFromUserId());
                    String fromUserNickname = fromUser != null ? fromUser.getNickname() : "未知用户";

                    String content = msg.getContent();
                    if (msg.getMessageType() == 4 && content != null && content.contains("|")) {
                        content = content.split("\\|")[0];
                    }

                    return MessageVO.builder()
                            .id(msg.getId())
                            .fromUserId(msg.getFromUserId())
                            .fromUserNickname(fromUserNickname)
                            .content(content)
                            .sendTime(msg.getSendTime())
                            .build();
                })
                .collect(Collectors.toList());

        log.info("下载聊天记录: userId={}, friendId={}, limit={}, actualSize={}", userId, friendId, limit, result.size());
        return result;
    }

    @Override
    public void markAsRead(Long userId, Long friendId) {
        throw new UnsupportedOperationException(
                "markAsRead is not supported by MessageQueryServiceImpl, use MessageOperateServiceImpl");
    }

    @Override
    public UnreadCountVO getUnreadCount(Long userId) {
        throw new UnsupportedOperationException(
                "getUnreadCount is not supported by MessageQueryServiceImpl, use MessageOperateServiceImpl");
    }

    @Override
    public void recallMessage(Long userId, Long messageId) {
        throw new UnsupportedOperationException(
                "recallMessage is not supported by MessageQueryServiceImpl, use MessageOperateServiceImpl");
    }
}
