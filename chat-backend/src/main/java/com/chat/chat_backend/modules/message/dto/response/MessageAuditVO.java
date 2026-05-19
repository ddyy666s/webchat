package com.chat.chat_backend.modules.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "消息审计视图对象（管理员查看所有消息记录）")
public class MessageAuditVO {
    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "发送方用户ID")
    private Long fromUserId;

    @Schema(description = "发送方昵称")
    private String fromUserNickname;

    @Schema(description = "接收方用户ID")
    private Long toUserId;

    @Schema(description = "接收方昵称")
    private String toUserNickname;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;
}
