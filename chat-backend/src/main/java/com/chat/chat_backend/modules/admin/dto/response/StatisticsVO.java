package com.chat.chat_backend.modules.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "系统统计数据视图对象")
public class StatisticsVO {
    @Schema(description = "总用户数")
    private Long totalUsers;

    @Schema(description = "今日活跃用户数")
    private Long todayActiveUsers;

    @Schema(description = "今日消息总数")
    private Long todayMessages;

    @Schema(description = "当前在线用户数")
    private Integer onlineUsers;
}
