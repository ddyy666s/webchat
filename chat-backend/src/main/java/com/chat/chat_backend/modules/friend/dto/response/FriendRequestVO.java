package com.chat.chat_backend.modules.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "好友请求视图对象")
public class FriendRequestVO {

    @Schema(description = "请求ID")
    private Long id;

    @Schema(description = "请求发起方用户ID")
    private Long fromUserId;

    @Schema(description = "请求发起方昵称")
    private String fromUserNickname;

    @Schema(description = "请求发起方头像URL")
    private String fromUserAvatar;

    @Schema(description = "验证消息")
    private String message;

    @Schema(description = "请求状态：0-待处理，1-已同意，2-已拒绝，3-已过期")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
