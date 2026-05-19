package com.chat.chat_backend.modules.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "好友视图对象")
public class FriendVO {

    @Schema(description = "好友关系ID")
    private Long id;

    @Schema(description = "好友用户ID")
    private Long userId;

    @Schema(description = "好友昵称")
    private String nickname;

    @Schema(description = "好友头像URL")
    private String avatar;

    @Schema(description = "好友个性签名")
    private String signature;

    @Schema(description = "好友备注")
    private String remark;

    @Schema(description = "分组名称")
    private String groupName;

    @Schema(description = "是否在线")
    private Boolean isOnline;

    @Schema(description = "未读消息数")
    private Integer unreadCount;
}
