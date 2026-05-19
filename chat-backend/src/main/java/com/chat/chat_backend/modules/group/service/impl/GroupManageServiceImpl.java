package com.chat.chat_backend.modules.group.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service("groupManageServiceImpl")
@RequiredArgsConstructor
public class GroupManageServiceImpl implements GroupService {

    private final GroupMapper groupMapper;
    private final GroupMemberMapper groupMemberMapper;
    private final GroupMuteService groupMuteService;
    private final GroupServiceHelper helper;

    @Override
    @Transactional
    public void updateNotice(Long userId, Long groupId, String notice) {
        Group group = helper.getGroupOrThrow(groupId);
        GroupMember member = helper.findMember(groupId, userId);
        if (member == null || (member.getRole() != 2 && member.getRole() != 1))
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权限修改群公告");
        group.setNotice(notice);
        groupMapper.updateById(group);
    }

    @Override
    @Transactional
    public void setAdmin(Long userId, Long groupId, Long memberId) {
        helper.checkOwner(groupId, userId);
        GroupMember member = helper.findMember(groupId, memberId);
        if (member == null) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该用户不是群成员");
        if (member.getRole() == 2) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不能设置群主为管理员");
        member.setRole(1);
        groupMemberMapper.updateById(member);
    }

    @Override
    @Transactional
    public void removeAdmin(Long userId, Long groupId, Long memberId) {
        helper.checkOwner(groupId, userId);
        GroupMember member = helper.findMember(groupId, memberId);
        if (member == null) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该用户不是群成员");
        if (member.getRole() != 1) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该用户不是管理员");
        member.setRole(0);
        groupMemberMapper.updateById(member);
    }

    @Override
    @Transactional
    public void removeMember(Long userId, Long groupId, Long memberId) {
        Group group = helper.getGroupOrThrow(groupId);
        GroupMember operator = helper.findMember(groupId, userId);
        if (operator == null || operator.getRole() == null || operator.getRole() == 0)
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权限移除成员");
        if (userId.equals(memberId)) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不能移除自己");
        GroupMember target = helper.findMember(groupId, memberId);
        if (target == null) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该用户不是群成员");
        Integer tr = target.getRole();
        if (tr == null || tr == 2) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不能移除群主");
        if (operator.getRole() == 1 && tr == 1)
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "管理员不能移除其他管理员");
        groupMemberMapper.deleteById(target.getId());
        group.setMemberCount(group.getMemberCount() - 1);
        groupMapper.updateById(group);
    }

    @Override
    public void muteMember(Long userId, Long groupId, Long memberId, Integer minutes) {
        groupMuteService.muteMember(userId, groupId, memberId, minutes);
    }

    @Override
    public void unmuteMember(Long userId, Long groupId, Long memberId) {
        groupMuteService.unmuteMember(userId, groupId, memberId);
    }

    @Override
    public void batchMute(Long userId, Long groupId, List<Long> memberIds, Integer minutes) {
        groupMuteService.batchMute(userId, groupId, memberIds, minutes);
    }

    @Override public GroupVO createGroup(Long userId, CreateGroupRequest r) { throw new UnsupportedOperationException(); }
    @Override public List<GroupVO> getUserGroups(Long userId) { throw new UnsupportedOperationException(); }
    @Override public GroupVO getGroupDetail(Long userId, Long groupId) { throw new UnsupportedOperationException(); }
    @Override public void inviteMember(Long userId, InviteMemberRequest r) { throw new UnsupportedOperationException(); }
    @Override public void quitGroup(Long userId, Long groupId) { throw new UnsupportedOperationException(); }
    @Override public void disbandGroup(Long userId, Long groupId) { throw new UnsupportedOperationException(); }
    @Override public void clearUnreadCount(Long userId, Long groupId) { throw new UnsupportedOperationException(); }
}
