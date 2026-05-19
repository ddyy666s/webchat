package com.chat.chat_backend.modules.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户资料更新请求参数")
public class UpdateProfileRequest {

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "个性签名")
    private String signature;
}
