package com.chat.chat_backend.modules.emoji.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("emoji")
@Schema(description = "表情实体")
public class Emoji {
    @TableId(type = IdType.AUTO)
    @Schema(description = "表情ID，自增主键")
    private Long id;

    @Schema(description = "表情名称")
    private String name;

    @Schema(description = "表情图片URL")
    private String url;

    @Schema(description = "表情分类")
    private String category;

    @Schema(description = "上传用户ID")
    private Long userId;

    @Schema(description = "是否系统表情：0-否，1-是")
    private Integer isSystem;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
