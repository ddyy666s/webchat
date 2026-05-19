package com.chat.chat_backend.modules.group.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "创建群组请求参数")
public class CreateGroupRequest {

    @NotBlank(message = "群组名称不能为空")
    @Schema(description = "群组名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "群组头像URL")
    private String avatar;

    @Schema(description = "群公告")
    private String notice;

    @Schema(description = "初始成员ID列表")
    private List<Long> memberIds;
}
