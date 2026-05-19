package com.chat.chat_backend.modules.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "未读消息统计响应结果")
public class UnreadCountVO {
    @Schema(description = "未读消息总数")
    private Integer total;

    @Schema(description = "各好友未读详情列表")
    private List<UnreadDetail> details;

    @Schema(description = "未读消息列表")
    private List<UnreadMessage> messages;

    @Data
    @Builder
    @Schema(description = "未读消息详情（按好友维度）")
    public static class UnreadDetail {
        @Schema(description = "好友用户ID")
        private Long friendId;

        @Schema(description = "好友昵称")
        private String friendNickname;

        @Schema(description = "好友头像URL")
        private String friendAvatar;

        @Schema(description = "未读消息数量")
        private Integer unreadCount;
    }

    @Data
    @Builder
    @Schema(description = "未读消息条目")
    public static class UnreadMessage {
        @Schema(description = "消息ID")
        private Long id;

        @Schema(description = "发送方用户ID")
        private Long fromUserId;

        @Schema(description = "发送方昵称")
        private String fromUserNickname;

        @Schema(description = "发送方头像URL")
        private String fromUserAvatar;

        @Schema(description = "消息类型：1-文字，2-图片，3-系统消息")
        private Integer messageType;

        @Schema(description = "消息内容")
        private String content;

        @Schema(description = "发送时间")
        private LocalDateTime sendTime;
    }
}
