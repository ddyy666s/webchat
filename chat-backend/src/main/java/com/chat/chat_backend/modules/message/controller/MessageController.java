package com.chat.chat_backend.modules.message.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.chat_backend.common.constant.MessageConstants;
import com.chat.chat_backend.common.result.Result;
import com.chat.chat_backend.modules.message.dto.response.MessageVO;
import com.chat.chat_backend.modules.message.dto.response.UnreadCountVO;
import com.chat.chat_backend.modules.message.service.MessageService;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
@Tag(name = "消息管理", description = "私聊消息的查询、下载、已读、撤回接口")
public class MessageController {

    private final MessageService messageService;
    private final UserMapper userMapper;

    @GetMapping("/history/{friendId}")
    @Operation(summary = "获取聊天记录", description = "分页查询与指定好友的聊天历史")
    public Result<Page<MessageVO>> getChatHistory(HttpServletRequest request,
                                                  @PathVariable Long friendId,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "20") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        Page<MessageVO> result = messageService.getChatHistory(userId, friendId, page, size);
        return Result.success(result);
    }

    @GetMapping("/download/{friendId}")
    @Operation(summary = "下载聊天记录", description = "导出与指定好友的聊天记录为文本文件")
    public void downloadChatHistory(HttpServletRequest request,
                                    HttpServletResponse response,
                                    @PathVariable Long friendId,
                                    @RequestParam(value = "limit", defaultValue = "100") Integer limit) throws Exception {
        Long userId = (Long) request.getAttribute("userId");

        log.info("下载聊天记录: userId={}, friendId={}, limit={}", userId, friendId, limit);

        if (limit > MessageConstants.MAX_DOWNLOAD_SIZE) {
            limit = MessageConstants.MAX_DOWNLOAD_SIZE;
        }
        if (limit <= 0) {
            limit = MessageConstants.DEFAULT_DOWNLOAD_SIZE;
        }

        List<MessageVO> messages = messageService.downloadChatHistory(userId, friendId, limit);

        if (messages == null || messages.isEmpty()) {
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("暂无聊天记录可导出");
            return;
        }

        User friend = userMapper.selectById(friendId);
        String friendName = friend != null ? friend.getNickname() : "好友";

        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(friendName + "_聊天记录_" + System.currentTimeMillis() + ".txt", "UTF-8"));

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("聊天记录导出\n");
        sb.append("导出时间: ").append(new Date()).append("\n");
        sb.append("聊天对象: ").append(friendName).append("\n");
        sb.append("共 ").append(messages.size()).append(" 条消息\n");
        sb.append("========================================\n\n");

        for (MessageVO msg : messages) {
            sb.append("[").append(msg.getSendTime()).append("] ");
            sb.append(msg.getFromUserNickname()).append(": ");
            sb.append(msg.getContent()).append("\n");
        }

        response.getWriter().write(sb.toString());
    }

    @PutMapping("/read/{friendId}")
    @Operation(summary = "标记消息已读", description = "将指定好友发送的所有消息标记为已读")
    public Result<Void> markAsRead(HttpServletRequest request, @PathVariable Long friendId) {
        Long userId = (Long) request.getAttribute("userId");
        messageService.markAsRead(userId, friendId);
        return Result.success("已标记已读", null);
    }

    @GetMapping("/unread/count")
    @Operation(summary = "获取未读消息统计", description = "获取用户的未读消息总数及详情")
    public Result<UnreadCountVO> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UnreadCountVO result = messageService.getUnreadCount(userId);
        return Result.success(result);
    }

    @PutMapping("/recall/{messageId}")
    @Operation(summary = "撤回消息", description = "撤回指定消息（仅发送者且在2分钟内可操作）")
    public Result<Void> recallMessage(HttpServletRequest request, @PathVariable Long messageId) {
        Long userId = (Long) request.getAttribute("userId");
        messageService.recallMessage(userId, messageId);
        return Result.success("撤回成功", null);
    }
}
