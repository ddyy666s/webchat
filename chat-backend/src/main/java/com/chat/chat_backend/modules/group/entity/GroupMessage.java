package com.chat.chat_backend.modules.group.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("group_message")
@Schema(description = "群消息实体")
public class GroupMessage {
    @TableId(type = IdType.AUTO)
    @Schema(description = "消息ID，自增主键")
    private Long id;
    @Schema(description = "群组ID")
    private Long groupId;
    @Schema(description = "发送方用户ID")
    private Long fromUserId;
    @Schema(description = "消息类型：1-文字，2-图片，3-文件")
    private Integer messageType;
    @Schema(description = "消息内容")
    private String content;
    @Schema(description = "发送时间")
    private LocalDateTime sendTime;
    @Schema(description = "撤回时间，为空表示未撤回")
    private LocalDateTime recallTime;
}