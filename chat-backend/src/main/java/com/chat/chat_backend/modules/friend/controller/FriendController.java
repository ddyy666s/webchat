package com.chat.chat_backend.modules.friend.controller;

import com.chat.chat_backend.common.result.Result;
import com.chat.chat_backend.modules.friend.dto.request.HandleFriendRequest;
import com.chat.chat_backend.modules.friend.dto.request.MoveFriendGroupRequest;
import com.chat.chat_backend.modules.friend.dto.request.SendFriendRequest;
import com.chat.chat_backend.modules.friend.dto.response.FriendGroupVO;
import com.chat.chat_backend.modules.friend.dto.response.FriendRequestVO;
import com.chat.chat_backend.modules.friend.dto.response.FriendVO;
import com.chat.chat_backend.modules.friend.service.FriendRelationService;
import com.chat.chat_backend.modules.friend.service.FriendRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 好友控制器，提供搜索用户、好友管理、好友请求等接口
 * @author chat-backend
 * @since 2026-05-12
 */
@Tag(name = "好友管理", description = "搜索用户、好友列表、好友请求、分组管理等接口")
@Slf4j
@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendRelationService friendRelationService;
    private final FriendRequestService friendRequestService;

    /** 搜索用户 @param request HTTP请求 @param keyword 关键词 @return 好友列表 */
    @Operation(summary = "搜索用户", description = "按用户名或昵称模糊搜索用户，排除当前用户")
    @GetMapping("/search")
    public Result<List<FriendVO>> searchUsers(HttpServletRequest request, @RequestParam String keyword) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(friendRelationService.searchUsers(userId, keyword));
    }

    /** 发送好友申请 @param request HTTP请求 @param req 好友申请参数 @return 操作结果 */
    @Operation(summary = "发送好友申请", description = "向指定用户发送好友请求")
    @PostMapping("/request")
    public Result<Void> sendFriendRequest(HttpServletRequest request, @RequestBody SendFriendRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        friendRequestService.sendFriendRequest(userId, req);
        return Result.success("好友申请已发送", null);
    }

    /** 获取好友申请列表 @param request HTTP请求 @return 好友申请列表 */
    @Operation(summary = "获取好友申请列表", description = "获取当前用户收到的待处理好友请求")
    @GetMapping("/requests")
    public Result<List<FriendRequestVO>> getFriendRequests(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(friendRequestService.getFriendRequests(userId));
    }

    /** 处理好友申请 @param request HTTP请求 @param requestId 申请ID @param req 处理参数 @return 操作结果 */
    @Operation(summary = "处理好友申请", description = "同意或拒绝好友申请")
    @PutMapping("/request/{requestId}")
    public Result<Void> handleFriendRequest(HttpServletRequest request,
                                            @PathVariable Long requestId,
                                            @RequestBody HandleFriendRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        friendRequestService.handleFriendRequest(userId, requestId, req);
        String msg = req.getStatus() == 1 ? "已同意" : "已拒绝";
        return Result.success(msg, null);
    }

    /** 获取好友列表（按分组） @param request HTTP请求 @return 好友分组列表 */
    @Operation(summary = "获取好友列表", description = "按分组获取好友列表，含在线状态和未读消息数")
    @GetMapping("/list")
    public Result<List<FriendGroupVO>> getFriendList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(friendRelationService.getFriendList(userId));
    }

    /** 删除好友 @param request HTTP请求 @param friendId 好友ID @return 操作结果 */
    @Operation(summary = "删除好友", description = "双向删除好友关系")
    @DeleteMapping("/{friendId}")
    public Result<Void> deleteFriend(HttpServletRequest request, @PathVariable Long friendId) {
        Long userId = (Long) request.getAttribute("userId");
        friendRelationService.deleteFriend(userId, friendId);
        return Result.success("删除成功", null);
    }

    /** 移动好友到其他分组 @param request HTTP请求 @param friendId 好友ID @param req 分组参数 @return 操作结果 */
    @Operation(summary = "移动好友分组", description = "将好友移动到其他分组")
    @PutMapping("/{friendId}/group")
    public Result<Void> moveFriendGroup(HttpServletRequest request,
                                        @PathVariable Long friendId,
                                        @RequestBody MoveFriendGroupRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        friendRelationService.moveFriendGroup(userId, friendId, req);
        return Result.success("移动成功", null);
    }

    /** 修改好友备注 @param request HTTP请求 @param friendId 好友ID @param remark 备注 @return 操作结果 */
    @Operation(summary = "修改好友备注", description = "修改好友的备注名称")
    @PutMapping("/{friendId}/remark")
    public Result<Void> updateFriendRemark(HttpServletRequest request,
                                           @PathVariable Long friendId,
                                           @RequestParam String remark) {
        Long userId = (Long) request.getAttribute("userId");
        friendRelationService.updateFriendRemark(userId, friendId, remark);
        return Result.success("修改成功", null);
    }
}
