package com.chat.chat_backend.modules.group.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.common.result.ResultCode;
import com.chat.chat_backend.modules.group.dto.request.CreateGroupRequest;
import com.chat.chat_backend.modules.group.dto.request.InviteMemberRequest;
import com.chat.chat_backend.modules.group.dto.response.GroupVO;
import com.chat.chat_backend.modules.group.entity.Group;
import com.chat.chat_backend.modules.group.entity.GroupMember;
import com.chat.chat_backend.modules.group.mapper.GroupMapper;
import com.chat.chat_backend.modules.group.mapper.GroupMemberMapper;
import com.chat.chat_backend.modules.group.service.GroupMuteService;
import com.chat.chat_backend.modules.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j @Primary @Service @RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupMapper groupMapper;
    private final GroupMemberMapper groupMemberMapper;
    private final GroupMuteService groupMuteService;
    private final GroupServiceHelper helper;

    @Override @Transactional
    public GroupVO createGroup(Long userId, CreateGroupRequest request) {
        Group group = new Group();
        group.setName(request.getName()); group.setAvatar(request.getAvatar()); group.setOwnerId(userId);
        group.setNotice(request.getNotice());
        group.setMemberCount(1 + (request.getMemberIds() != null ? request.getMemberIds().size() : 0));
        group.setCreatedAt(LocalDateTime.now()); groupMapper.insert(group);
        helper.insertMember(group.getId(), userId, 2);
        if (request.getMemberIds() != null)
            request.getMemberIds().stream().filter(mid -> !mid.equals(userId)).forEach(mid -> helper.insertMember(group.getId(), mid, 0));
        log.info("用户 {} 创建了群聊: {}", userId, group.getName());
        return getGroupDetail(userId, group.getId());
    }
    @Override
    public List<GroupVO> getUserGroups(Long userId) {
        List<Group> groups = groupMapper.findGroupsByUserId(userId);
        if (groups.isEmpty()) return new ArrayList<>();
        Map<Long, Integer> unreadMap = groupMemberMapper.selectList(new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getUserId, userId).in(GroupMember::getGroupId, groups.stream().map(Group::getId).collect(Collectors.toList()))).stream().collect(Collectors.toMap(GroupMember::getGroupId, GroupMember::getUnreadCount));
        return groups.stream().map(g -> GroupVO.builder().id(g.getId()).name(g.getName()).avatar(g.getAvatar()).notice(g.getNotice()).ownerId(g.getOwnerId()).memberCount(g.getMemberCount()).unreadCount(unreadMap.getOrDefault(g.getId(), 0)).createdAt(g.getCreatedAt()).build()).collect(Collectors.toList());
    }
    @Override
    public GroupVO getGroupDetail(Long userId, Long groupId) { Group group = helper.getGroupOrThrow(groupId); helper.checkMember(groupId, userId); return helper.toGroupVO(group); }
    @Override @Transactional
    public void inviteMember(Long userId, InviteMemberRequest request) {
        Group group = helper.getGroupOrThrow(request.getGroupId());
        helper.checkMember(request.getGroupId(), userId);
        if (helper.findMember(request.getGroupId(), request.getUserId()) != null) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "用户已在群中");
        helper.insertMember(request.getGroupId(), request.getUserId(), 0);
        group.setMemberCount(group.getMemberCount() + 1); groupMapper.updateById(group);
    }
    @Override @Transactional
    public void quitGroup(Long userId, Long groupId) {
        Group group = helper.getGroupOrThrow(groupId);
        if (group.getOwnerId().equals(userId)) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "群主不能退群");
        groupMemberMapper.delete(new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId).eq(GroupMember::getUserId, userId));
        group.setMemberCount(group.getMemberCount() - 1); groupMapper.updateById(group);
    }
    @Override @Transactional
    public void disbandGroup(Long userId, Long groupId) {
        Group group = helper.getGroupOrThrow(groupId);
        if (!group.getOwnerId().equals(userId)) throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "只有群主可以解散群聊");
        groupMemberMapper.delete(new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupId, groupId));
        groupMapper.deleteById(groupId);
    }
    @Override
    public void clearUnreadCount(Long userId, Long groupId) { groupMemberMapper.clearUnreadCount(groupId, userId); }
    @Override @Transactional
    public void updateNotice(Long userId, Long groupId, String notice) {
        Group group = helper.getGroupOrThrow(groupId);
        GroupMember member = helper.findMember(groupId, userId);
        if (member == null || (member.getRole() != 2 && member.getRole() != 1)) throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权限修改群公告");
        group.setNotice(notice); groupMapper.updateById(group);
    }
    @Override @Transactional
    public void setAdmin(Long userId, Long groupId, Long memberId) {
        helper.checkOwner(groupId, userId);
        GroupMember member = helper.findMember(groupId, memberId);
        if (member == null) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该用户不是群成员");
        if (member.getRole() == 2) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不能设置群主为管理员");
        member.setRole(1); groupMemberMapper.updateById(member);
    }
    @Override @Transactional
    public void removeAdmin(Long userId, Long groupId, Long memberId) {
        helper.checkOwner(groupId, userId);
        GroupMember member = helper.findMember(groupId, memberId);
        if (member == null) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该用户不是群成员");
        if (member.getRole() != 1) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该用户不是管理员");
        member.setRole(0); groupMemberMapper.updateById(member);
    }
    @Override @Transactional
    public void removeMember(Long userId, Long groupId, Long memberId) {
        Group group = helper.getGroupOrThrow(groupId);
        GroupMember operator = helper.findMember(groupId, userId);
        if (operator == null || operator.getRole() == null || operator.getRole() == 0) throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权限移除成员");
        if (userId.equals(memberId)) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不能移除自己");
        GroupMember target = helper.findMember(groupId, memberId);
        if (target == null || target.getRole() == null || target.getRole() == 2) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不能移除群主");
        if (operator.getRole() == 1 && target.getRole() == 1) throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "管理员不能移除其他管理员");
        groupMemberMapper.deleteById(target.getId());
        group.setMemberCount(group.getMemberCount() - 1); groupMapper.updateById(group);
    }
    @Override
    public void muteMember(Long userId, Long groupId, Long memberId, Integer minutes) { groupMuteService.muteMember(userId, groupId, memberId, minutes); }
    @Override
    public void unmuteMember(Long userId, Long groupId, Long memberId) { groupMuteService.unmuteMember(userId, groupId, memberId); }
    @Override
    public void batchMute(Long userId, Long groupId, List<Long> memberIds, Integer minutes) { groupMuteService.batchMute(userId, groupId, memberIds, minutes); }
}
