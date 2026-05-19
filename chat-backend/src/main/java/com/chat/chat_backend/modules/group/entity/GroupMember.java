package com.chat.chat_backend.modules.group.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("group_member")
@Schema(description = "群成员实体")
public class GroupMember {
    @TableId(type = IdType.AUTO)
    @Schema(description = "成员关系ID，自增主键")
    private Long id;
    @Schema(description = "群组ID")
    private Long groupId;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "群内昵称")
    private String nickname;
    @Schema(description = "角色：0-普通成员，1-群管理员，2-群主")
    private Integer role;
    @Schema(description = "未读消息数")
    private Integer unreadCount;
    @Schema(description = "加入时间")
    private LocalDateTime joinTime;
    @Schema(description = "最后阅读时间")
    private LocalDateTime lastReadTime;
    @TableField(exist = false)
    @Schema(description = "禁言截止时间，为空表示未禁言")
    private LocalDateTime muteUntil;
}