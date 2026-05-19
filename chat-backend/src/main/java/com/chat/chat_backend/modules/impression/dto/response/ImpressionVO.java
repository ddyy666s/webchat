package com.chat.chat_backend.modules.impression.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "印象/评价视图对象")
public class ImpressionVO {
    @Schema(description = "印象ID")
    private Long id;

    @Schema(description = "评价方用户ID")
    private Long fromUserId;

    @Schema(description = "评价方昵称")
    private String fromUserNickname;

    @Schema(description = "评价方头像URL")
    private String fromUserAvatar;

    @Schema(description = "被评价方用户ID")
    private Long toUserId;

    @Schema(description = "被评价方昵称")
    private String toUserNickname;

    @Schema(description = "被评价方头像URL")
    private String toUserAvatar;

    @Schema(description = "印象内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
