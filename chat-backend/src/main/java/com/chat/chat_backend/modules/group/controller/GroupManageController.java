package com.chat.chat_backend.modules.group.controller;

import com.chat.chat_backend.common.result.Result;
import com.chat.chat_backend.modules.group.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "群管理操作", description = "公告、管理员设置、禁言、移除成员等管理操作")
@RestController
@RequestMapping("/group")
public class GroupManageController {

    private final GroupService groupManageService;

    public GroupManageController(@Qualifier("groupManageServiceImpl") GroupService groupManageService) {
        this.groupManageService = groupManageService;
    }

    @Operation(summary = "更新群公告")
    @PutMapping("/{groupId}/notice")
    public Result<Void> updateNotice(HttpServletRequest request,
                                     @PathVariable Long groupId,
                                     @RequestBody Map<String, String> body) {
        Long userId = (Long) request.getAttribute("userId");
        groupManageService.updateNotice(userId, groupId, body.get("notice"));
        return Result.success("群公告已更新", null);
    }

    @Operation(summary = "设置管理员")
    @PutMapping("/{groupId}/member/{memberId}/set-admin")
    public Result<Void> setAdmin(HttpServletRequest request,
                                 @PathVariable Long groupId,
                                 @PathVariable Long memberId) {
        Long userId = (Long) request.getAttribute("userId");
        groupManageService.setAdmin(userId, groupId, memberId);
        return Result.success("已设置管理员", null);
    }

    @Operation(summary = "取消管理员")
    @PutMapping("/{groupId}/member/{memberId}/remove-admin")
    public Result<Void> removeAdmin(HttpServletRequest request,
                                    @PathVariable Long groupId,
                                    @PathVariable Long memberId) {
        Long userId = (Long) request.getAttribute("userId");
        groupManageService.removeAdmin(userId, groupId, memberId);
        return Result.success("已取消管理员", null);
    }

    @Operation(summary = "移除群成员")
    @DeleteMapping("/{groupId}/member/{memberId}")
    public Result<Void> removeMember(HttpServletRequest request,
                                     @PathVariable Long groupId,
                                     @PathVariable Long memberId) {
        Long userId = (Long) request.getAttribute("userId");
        groupManageService.removeMember(userId, groupId, memberId);
        return Result.success("已移除成员", null);
    }

    @Operation(summary = "禁言成员")
    @PutMapping("/{groupId}/member/{memberId}/mute")
    public Result<Void> muteMember(HttpServletRequest request,
                                   @PathVariable Long groupId,
                                   @PathVariable Long memberId,
                                   @RequestBody Map<String, Object> body) {
        Long userId = (Long) request.getAttribute("userId");
        Integer minutes = body.get("minutes") instanceof Integer ? (Integer) body.get("minutes") : 60;
        groupManageService.muteMember(userId, groupId, memberId, minutes);
        return Result.success("已禁言", null);
    }

    @Operation(summary = "取消禁言")
    @PutMapping("/{groupId}/member/{memberId}/unmute")
    public Result<Void> unmuteMember(HttpServletRequest request,
                                     @PathVariable Long groupId,
                                     @PathVariable Long memberId) {
        Long userId = (Long) request.getAttribute("userId");
        groupManageService.unmuteMember(userId, groupId, memberId);
        return Result.success("已取消禁言", null);
    }

    @Operation(summary = "批量禁言")
    @PutMapping("/{groupId}/members/batch-mute")
    public Result<Void> batchMute(HttpServletRequest request,
                                  @PathVariable Long groupId,
                                  @RequestBody Map<String, Object> body) {
        Long userId = (Long) request.getAttribute("userId");
        @SuppressWarnings("unchecked")
        List<Integer> memberIds = (List<Integer>) body.get("memberIds");
        Integer minutes = body.get("minutes") instanceof Integer ? (Integer) body.get("minutes") : 60;
        groupManageService.batchMute(userId, groupId,
                memberIds.stream().map(Long::valueOf).collect(Collectors.toList()), minutes);
        return Result.success("已批量禁言", null);
    }
}
