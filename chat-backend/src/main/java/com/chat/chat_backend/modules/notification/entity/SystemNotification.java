package com.chat.chat_backend.modules.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("system_notification")
@Schema(description = "系统通知实体")
public class SystemNotification {
    @TableId(type = IdType.AUTO)
    @Schema(description = "通知ID，自增主键")
    private Long id;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "发布管理员ID")
    private Long adminId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
