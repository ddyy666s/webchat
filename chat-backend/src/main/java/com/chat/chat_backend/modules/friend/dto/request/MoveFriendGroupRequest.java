package com.chat.chat_backend.modules.friend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "移动好友分组请求参数")
public class MoveFriendGroupRequest {

    @NotBlank(message = "分组名称不能为空")
    @Schema(description = "目标分组名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String groupName;
}
