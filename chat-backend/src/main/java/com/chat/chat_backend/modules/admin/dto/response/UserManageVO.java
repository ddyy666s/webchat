package com.chat.chat_backend.modules.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "用户管理视图对象（管理员使用）")
public class UserManageVO {
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "角色：USER-普通用户，ADMIN-管理员")
    private String role;

    @Schema(description = "状态：0-正常，1-禁用")
    private Integer status;

    @Schema(description = "注册时间")
    private LocalDateTime createdAt;
}
