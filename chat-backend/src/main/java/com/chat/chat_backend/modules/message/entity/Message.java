package com.chat.chat_backend.modules.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("message")
@Schema(description = "私聊消息实体")
public class Message {
    @TableId(type = IdType.AUTO)
    @Schema(description = "消息ID，自增主键")
    private Long id;

    @Schema(description = "发送方用户ID")
    private Long fromUserId;

    @Schema(description = "接收方用户ID")
    private Long toUserId;

    @Schema(description = "消息类型：1-文字，2-图片，3-文件，4-语音")
    private Integer messageType;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "是否已读：0-未读，1-已读")
    private Integer isRead;

    @Schema(description = "读取时间")
    private LocalDateTime readTime;

    @Schema(description = "撤回时间，为空表示未撤回")
    private LocalDateTime recallTime;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;
}
