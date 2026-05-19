package com.chat.chat_backend.modules.group.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "群成员视图对象")
public class GroupMemberVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像URL")
    private String avatar;

    @Schema(description = "群内昵称")
    private String groupNickname;

    @Schema(description = "角色：0-普通成员，1-群管理员，2-群主")
    private Integer role;

    @Schema(description = "是否被禁言")
    private Boolean muted;
}
