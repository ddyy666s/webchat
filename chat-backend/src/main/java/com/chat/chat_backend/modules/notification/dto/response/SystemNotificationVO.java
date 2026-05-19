package com.chat.chat_backend.modules.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "系统通知视图对象")
public class SystemNotificationVO {
    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "发布管理员ID")
    private Long adminId;

    @Schema(description = "发布管理员昵称")
    private String adminNickname;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
