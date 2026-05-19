package com.chat.chat_backend.modules.friend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chat.chat_backend.common.constant.RedisConstants;
import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.common.utils.RedisUtil;
import com.chat.chat_backend.modules.friend.dto.request.MoveFriendGroupRequest;
import com.chat.chat_backend.modules.friend.dto.response.FriendVO;
import com.chat.chat_backend.modules.friend.entity.Friend;
import com.chat.chat_backend.modules.friend.mapper.FriendMapper;
import com.chat.chat_backend.modules.friend.service.impl.FriendRelationServiceImpl;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendRelationServiceTest {

    @Mock private FriendMapper friendMapper;
    @Mock private UserMapper userMapper;
    @Mock private RedisUtil redisUtil;
    @Mock private RedisTemplate<String, Object> redisTemplate;
    @Mock private HashOperations<String, Object, Object> hashOperations;
    @InjectMocks private FriendRelationServiceImpl friendRelationService;

    @Test
    void searchUsers_withResults_shouldReturnList() {
        User user = new User();
        user.setId(2L);
        user.setNickname("testUser");
        when(userMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(user));
        when(friendMapper.findFriendRelation(anyLong(), anyLong())).thenReturn(null);
        when(redisUtil.isMember(anyString(), anyString())).thenReturn(false);

        List<FriendVO> result = friendRelationService.searchUsers(1L, "test");
        assertEquals(1, result.size());
        assertEquals("testUser", result.get(0).getNickname());
    }

    @Test
    void searchUsers_withEmptyKeyword_shouldReturnEmpty() {
        when(userMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());
        List<FriendVO> result = friendRelationService.searchUsers(1L, "nonexistent");
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteFriend_shouldDeleteBothDirections() {
        friendRelationService.deleteFriend(1L, 2L);
        verify(friendMapper, times(2)).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    void moveFriendGroup_whenFriendNotFound_shouldThrow() {
        when(friendMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        MoveFriendGroupRequest req = new MoveFriendGroupRequest();
        req.setGroupName("同事");
        assertThrows(BusinessException.class, () -> friendRelationService.moveFriendGroup(1L, 2L, req));
    }

    @Test
    void moveFriendGroup_whenFriendFound_shouldUpdate() {
        Friend friend = new Friend();
        friend.setId(1L);
        when(friendMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(friend);
        MoveFriendGroupRequest req = new MoveFriendGroupRequest();
        req.setGroupName("同事");
        friendRelationService.moveFriendGroup(1L, 2L, req);
        assertEquals("同事", friend.getGroupName());
        verify(friendMapper).updateById(friend);
    }

    @Test
    void updateFriendRemark_whenFriendNotFound_shouldThrow() {
        when(friendMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        assertThrows(BusinessException.class, () -> friendRelationService.updateFriendRemark(1L, 2L, "remark"));
    }

    @Test
    void updateFriendRemark_whenFriendFound_shouldUpdate() {
        Friend friend = new Friend();
        friend.setId(1L);
        when(friendMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(friend);
        friendRelationService.updateFriendRemark(1L, 2L, "newRemark");
        assertEquals("newRemark", friend.getRemark());
        verify(friendMapper).updateById(friend);
    }
}
