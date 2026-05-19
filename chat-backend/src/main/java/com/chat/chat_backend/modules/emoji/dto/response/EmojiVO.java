package com.chat.chat_backend.modules.emoji.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "表情视图对象")
public class EmojiVO {
    @Schema(description = "表情ID")
    private Long id;

    @Schema(description = "表情名称")
    private String name;

    @Schema(description = "表情图片URL")
    private String url;

    @Schema(description = "表情分类")
    private String category;

    @Schema(description = "上传用户ID")
    private Long userId;

    @Schema(description = "是否系统表情")
    private Boolean isSystem;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
