package com.chat.chat_backend.modules.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("notification_read")
@Schema(description = "通知已读记录实体")
public class NotificationRead {
    @TableId(type = IdType.AUTO)
    @Schema(description = "记录ID，自增主键")
    private Long id;

    @Schema(description = "系统通知ID")
    private Long notificationId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "读取时间")
    private LocalDateTime readAt;
}
