package com.chat.chat_backend.modules.group.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.common.result.ResultCode;
import com.chat.chat_backend.modules.group.dto.response.GroupVO;
import com.chat.chat_backend.modules.group.entity.Group;
import com.chat.chat_backend.modules.group.entity.GroupMember;
import com.chat.chat_backend.modules.group.mapper.GroupMapper;
import com.chat.chat_backend.modules.group.mapper.GroupMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GroupServiceHelper {

    private final GroupMapper groupMapper;
    private final GroupMemberMapper groupMemberMapper;

    public Group getGroupOrThrow(Long groupId) {
        Group group = groupMapper.selectById(groupId);
        if (group == null) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "群聊不存在");
        return group;
    }

    public GroupMember findMember(Long groupId, Long userId) {
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupId, groupId).eq(GroupMember::getUserId, userId);
        return groupMemberMapper.selectOne(wrapper);
    }

    public void checkMember(Long groupId, Long userId) {
        if (groupMemberMapper.selectCount(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupId, groupId).eq(GroupMember::getUserId, userId)) == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "你不是群成员");
        }
    }

    public void checkOwner(Long groupId, Long userId) {
        if (!getGroupOrThrow(groupId).getOwnerId().equals(userId))
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "只有群主可以操作");
    }

    public void insertMember(Long groupId, Long userId, Integer role) {
        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setRole(role);
        member.setJoinTime(LocalDateTime.now());
        groupMemberMapper.insert(member);
    }

    public GroupVO toGroupVO(Group group) {
        return GroupVO.builder().id(group.getId()).name(group.getName()).avatar(group.getAvatar())
                .notice(group.getNotice()).ownerId(group.getOwnerId())
                .memberCount(group.getMemberCount()).createdAt(group.getCreatedAt()).build();
    }
}
