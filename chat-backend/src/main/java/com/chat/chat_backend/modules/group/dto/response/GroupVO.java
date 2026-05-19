package com.chat.chat_backend.modules.group.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "群组视图对象")
public class GroupVO {

    @Schema(description = "群组ID")
    private Long id;

    @Schema(description = "群组名称")
    private String name;

    @Schema(description = "群组头像URL")
    private String avatar;

    @Schema(description = "群公告")
    private String notice;

    @Schema(description = "群主用户ID")
    private Long ownerId;

    @Schema(description = "成员数量")
    private Integer memberCount;

    @Schema(description = "当前用户未读消息数")
    private Integer unreadCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "成员列表")
    private List<GroupMemberVO> members;
}
