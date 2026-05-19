package com.chat.chat_backend.modules.friend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("friend_request")
@Schema(description = "好友请求实体")
public class FriendRequest {

    @TableId(type = IdType.AUTO)
    @Schema(description = "请求ID，自增主键")
    private Long id;

    @Schema(description = "请求发起方用户ID")
    private Long fromUserId;

    @Schema(description = "请求接收方用户ID")
    private Long toUserId;

    @Schema(description = "验证消息")
    private String message;

    @Schema(description = "请求状态：0-待处理，1-已同意，2-已拒绝，3-已过期")
    private Integer status;

    @Schema(description = "处理时间")
    private LocalDateTime handledTime;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
