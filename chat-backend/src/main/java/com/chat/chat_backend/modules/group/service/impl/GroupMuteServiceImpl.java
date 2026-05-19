package com.chat.chat_backend.modules.group.service.impl;

import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.common.result.ResultCode;
import com.chat.chat_backend.modules.group.entity.GroupMember;
import com.chat.chat_backend.modules.group.mapper.GroupMemberMapper;
import com.chat.chat_backend.modules.group.service.GroupMuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupMuteServiceImpl implements GroupMuteService {

    private static final Map<String, LocalDateTime> muteMap = new ConcurrentHashMap<>();

    private final GroupMemberMapper groupMemberMapper;

    private static String muteKey(Long groupId, Long userId) {
        return groupId + ":" + userId;
    }

    @Override
    public boolean isMuted(Long groupId, Long userId) {
        LocalDateTime until = muteMap.get(muteKey(groupId, userId));
        return until != null && until.isAfter(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void muteMember(Long userId, Long groupId, Long memberId, Integer minutes) {
        GroupMember operator = checkAdminOrOwner(userId, groupId);
        GroupMember target = findMember(groupId, memberId);
        if (target == null) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该用户不是群成员");
        Integer targetRole = target.getRole();
        if (targetRole == null || targetRole == 2) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不能禁言群主");
        if (operator.getRole() == 1 && targetRole == 1)
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "管理员不能禁言其他管理员");
        muteMap.put(muteKey(groupId, memberId), LocalDateTime.now().plusMinutes(minutes));
    }

    @Override
    @Transactional
    public void unmuteMember(Long userId, Long groupId, Long memberId) {
        GroupMember operator = checkAdminOrOwner(userId, groupId);
        GroupMember target = findMember(groupId, memberId);
        if (target == null) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该用户不是群成员");
        Integer targetRole = target.getRole();
        if (targetRole == null || targetRole == 2) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不能操作群主");
        if (operator.getRole() == 1 && targetRole == 1)
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "管理员不能操作其他管理员");
        muteMap.remove(muteKey(groupId, memberId));
    }

    @Override
    @Transactional
    public void batchMute(Long userId, Long groupId, List<Long> memberIds, Integer minutes) {
        GroupMember operator = checkAdminOrOwner(userId, groupId);
        LocalDateTime until = LocalDateTime.now().plusMinutes(minutes);
        for (Long mid : memberIds) {
            GroupMember target = findMember(groupId, mid);
            if (target == null) continue;
            Integer targetRole = target.getRole();
            if (targetRole == null || targetRole == 2) continue;
            if (operator.getRole() == 1 && targetRole == 1) continue;
            muteMap.put(muteKey(groupId, mid), until);
        }
    }

    private GroupMember checkAdminOrOwner(Long userId, Long groupId) {
        GroupMember operator = findMember(groupId, userId);
        if (operator == null) throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "你不是群成员");
        if (operator.getRole() == null || operator.getRole() == 0)
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权限");
        return operator;
    }

    private GroupMember findMember(Long groupId, Long userId) {
        return groupMemberMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getGroupId, groupId)
                        .eq(GroupMember::getUserId, userId));
    }
}
