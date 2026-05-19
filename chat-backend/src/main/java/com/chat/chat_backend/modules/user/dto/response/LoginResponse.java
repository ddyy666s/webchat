package com.chat.chat_backend.modules.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "登录响应结果")
public class LoginResponse {

    @Schema(description = "JWT认证令牌")
    private String token;

    @Schema(description = "登录用户基本信息")
    private UserInfoResponse user;
}
