package com.chat.chat_backend.modules.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "私聊消息视图对象")
public class MessageVO {
    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "发送方用户ID")
    private Long fromUserId;

    @Schema(description = "发送方昵称")
    private String fromUserNickname;

    @Schema(description = "语音消息时长（秒），仅消息类型为语音时有效")
    private Integer duration;

    @Schema(description = "发送方头像URL")
    private String fromUserAvatar;

    @Schema(description = "接收方用户ID")
    private Long toUserId;

    @Schema(description = "接收方昵称")
    private String toUserNickname;

    @Schema(description = "消息类型：1-文字，2-图片，3-系统消息/评价通知")
    private Integer messageType;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "是否已读")
    private Boolean isRead;

    @Schema(description = "是否已撤回")
    private Boolean isRecalled;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;
}
