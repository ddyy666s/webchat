package com.chat.chat_backend.modules.group.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "群未读消息视图对象")
public class UnreadGroupVO {

    @Schema(description = "发送方用户ID")
    private Long fromUserId;

    @Schema(description = "未读消息数量")
    private Integer count;
}
