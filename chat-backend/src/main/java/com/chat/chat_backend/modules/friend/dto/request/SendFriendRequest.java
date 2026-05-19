package com.chat.chat_backend.modules.friend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "发送好友请求请求参数")
public class SendFriendRequest {

    @NotNull(message = "目标用户ID不能为空")
    @Schema(description = "目标用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long toUserId;

    @Schema(description = "验证消息")
    private String message;
}
