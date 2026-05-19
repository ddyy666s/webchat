package com.chat.chat_backend.modules.friend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "好友请求处理请求参数")
public class HandleFriendRequest {

    @NotNull(message = "处理状态不能为空")
    @Schema(description = "处理状态：1-同意，2-拒绝", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;
}
