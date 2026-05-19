package com.chat.chat_backend.modules.impression.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("impression")
@Schema(description = "印象/评价实体")
public class Impression {
    @TableId(type = IdType.AUTO)
    @Schema(description = "印象ID，自增主键")
    private Long id;

    @Schema(description = "评价方用户ID")
    private Long fromUserId;

    @Schema(description = "被评价方用户ID")
    private Long toUserId;

    @Schema(description = "印象内容")
    private String content;

    @Schema(description = "是否删除：0-未删除，1-已删除")
    private Integer isDelete;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
