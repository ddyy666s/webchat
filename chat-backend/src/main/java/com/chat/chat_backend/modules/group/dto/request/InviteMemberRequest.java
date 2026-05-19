package com.chat.chat_backend.modules.group.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "邀请群成员请求参数")
public class InviteMemberRequest {

    @NotNull(message = "群组ID不能为空")
    @Schema(description = "群组ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long groupId;

    @NotNull(message = "被邀请用户ID不能为空")
    @Schema(description = "被邀请用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;
}
