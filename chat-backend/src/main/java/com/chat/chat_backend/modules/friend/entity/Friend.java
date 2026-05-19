package com.chat.chat_backend.modules.friend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("friend")
@Schema(description = "好友关系实体")
public class Friend {

    @TableId(type = IdType.AUTO)
    @Schema(description = "好友关系ID，自增主键")
    private Long id;

    @Schema(description = "用户ID（所属方）")
    private Long userId;

    @Schema(description = "好友用户ID")
    private Long friendId;

    @Schema(description = "所属分组名称")
    private String groupName;

    @Schema(description = "好友备注")
    private String remark;

    @Schema(description = "是否置顶：0-否，1-是")
    private Integer isTop;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
