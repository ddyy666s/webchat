package com.chat.chat_backend.modules.impression.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "添加印象/评价请求参数")
public class AddImpressionRequest {

    @NotNull(message = "目标用户ID不能为空")
    @Schema(description = "目标用户ID")
    private Long toUserId;

    @NotBlank(message = "评价内容不能为空")
    @Size(max = 100, message = "评价内容不能超过100字")
    @Schema(description = "印象内容，最多100字")
    private String content;
}
