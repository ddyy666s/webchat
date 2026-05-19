package com.chat.chat_backend.modules.group.controller;

import com.chat.chat_backend.common.result.Result;
import com.chat.chat_backend.modules.group.dto.request.CreateGroupRequest;
import com.chat.chat_backend.modules.group.dto.request.InviteMemberRequest;
import com.chat.chat_backend.modules.group.dto.response.GroupMemberVO;
import com.chat.chat_backend.modules.group.dto.response.GroupVO;
import com.chat.chat_backend.modules.group.entity.GroupMember;
import com.chat.chat_backend.modules.group.mapper.GroupMemberMapper;
import com.chat.chat_backend.modules.group.service.GroupMuteService;
import com.chat.chat_backend.modules.group.service.GroupService;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "群聊管理", description = "创建群聊、查询列表、邀请成员、退出解散等基础操作")
@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final GroupMuteService groupMuteService;
    private final GroupMemberMapper groupMemberMapper;
    private final UserMapper userMapper;

    @Operation(summary = "创建群聊")
    @PostMapping
    public Result<GroupVO> createGroup(HttpServletRequest request, @Valid @RequestBody CreateGroupRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(groupService.createGroup(userId, req));
    }

    @Operation(summary = "获取用户的群聊列表")
    @GetMapping("/list")
    public Result<List<GroupVO>> getUserGroups(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(groupService.getUserGroups(userId));
    }

    @Operation(summary = "获取群详情")
    @GetMapping("/{groupId}")
    public Result<GroupVO> getGroupDetail(HttpServletRequest request, @PathVariable Long groupId) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(groupService.getGroupDetail(userId, groupId));
    }

    @Operation(summary = "邀请成员")
    @PostMapping("/invite")
    public Result<Void> inviteMember(HttpServletRequest request, @Valid @RequestBody InviteMemberRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        groupService.inviteMember(userId, req);
        return Result.success("邀请成功", null);
    }

    @Operation(summary = "退出群聊")
    @DeleteMapping("/{groupId}/quit")
    public Result<Void> quitGroup(HttpServletRequest request, @PathVariable Long groupId) {
        Long userId = (Long) request.getAttribute("userId");
        groupService.quitGroup(userId, groupId);
        return Result.success("已退出群聊", null);
    }

    @Operation(summary = "解散群聊")
    @DeleteMapping("/{groupId}/disband")
    public Result<Void> disbandGroup(HttpServletRequest request, @PathVariable Long groupId) {
        Long userId = (Long) request.getAttribute("userId");
        groupService.disbandGroup(userId, groupId);
        return Result.success("群聊已解散", null);
    }

    @Operation(summary = "获取群成员列表")
    @GetMapping("/{groupId}/members")
    public Result<List<GroupMemberVO>> getGroupMembers(@PathVariable Long groupId) {
        List<GroupMember> members = groupMemberMapper.findByGroupId(groupId);
        List<GroupMemberVO> result = members.stream().map(member -> {
            User user = userMapper.selectById(member.getUserId());
            return GroupMemberVO.builder()
                    .userId(member.getUserId())
                    .nickname(user != null ? user.getNickname() : "未知用户")
                    .avatar(user != null ? user.getAvatar() : null)
                    .groupNickname(member.getNickname())
                    .role(member.getRole())
                    .muted(groupMuteService.isMuted(groupId, member.getUserId()))
                    .build();
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    @Operation(summary = "清除群未读消息计数")
    @PutMapping("/{groupId}/read")
    public Result<Void> clearUnreadCount(HttpServletRequest request, @PathVariable Long groupId) {
        Long userId = (Long) request.getAttribute("userId");
        groupService.clearUnreadCount(userId, groupId);
        return Result.success("已清除未读", null);
    }
}
