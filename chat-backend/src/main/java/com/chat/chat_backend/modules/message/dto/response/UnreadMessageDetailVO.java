package com.chat.chat_backend.modules.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "未读消息详情视图对象")
public class UnreadMessageDetailVO {
    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "发送方用户ID")
    private Long fromUserId;

    @Schema(description = "发送方昵称")
    private String fromUserNickname;

    @Schema(description = "发送方头像URL")
    private String fromUserAvatar;

    @Schema(description = "消息类型：1-文字，2-图片，3-系统消息")
    private Integer messageType;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;
}
