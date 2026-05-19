package com.chat.chat_backend.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.chat_backend.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
@Schema(description = "用户实体")
public class User extends BaseEntity {

    @Schema(description = "用户名，唯一标识")
    private String username;

    @Schema(description = "密码（加密存储）")
    private String password;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "个性签名")
    private String signature;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "角色：USER-普通用户，ADMIN-管理员")
    private String role;

    @Schema(description = "状态：0-正常，1-禁用")
    private Integer status;
}
