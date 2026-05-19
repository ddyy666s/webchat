package com.chat.chat_backend.modules.group.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("chat_group")
@Schema(description = "群组实体")
public class Group {
    @TableId(type = IdType.AUTO)
    @Schema(description = "群组ID，自增主键")
    private Long id;
    @Schema(description = "群组名称")
    private String name;
    @Schema(description = "群组头像URL")
    private String avatar;
    @Schema(description = "群主用户ID")
    private Long ownerId;
    @Schema(description = "群公告")
    private String notice;
    @Schema(description = "成员数量")
    private Integer memberCount;
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}