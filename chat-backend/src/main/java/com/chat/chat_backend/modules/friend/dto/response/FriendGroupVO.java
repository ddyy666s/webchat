package com.chat.chat_backend.modules.friend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "好友分组视图对象")
public class FriendGroupVO {

    @Schema(description = "分组名称")
    private String groupName;

    @Schema(description = "该分组下的好友列表")
    private List<FriendVO> friends;
}
