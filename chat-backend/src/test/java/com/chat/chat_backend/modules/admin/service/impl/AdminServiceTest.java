package com.chat.chat_backend.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.chat_backend.common.constant.RedisConstants;
import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.common.utils.RedisUtil;
import com.chat.chat_backend.modules.admin.dto.response.StatisticsVO;
import com.chat.chat_backend.modules.admin.dto.response.UserManageVO;
import com.chat.chat_backend.modules.message.mapper.MessageMapper;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private RedisUtil redisUtil;
    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void getUserList_shouldReturnPaginatedResults() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setNickname("Test");
        user.setRole("USER");
        user.setStatus(0);
        user.setCreatedAt(LocalDateTime.now());

        Page<User> userPage = new Page<>(1, 10);
        userPage.setRecords(List.of(user));
        userPage.setTotal(1);

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(userPage);

        Page<UserManageVO> result = adminService.getUserList(1, 10, null);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("testuser", result.getRecords().get(0).getUsername());
    }

    @Test
    void getUserList_shouldFilterByKeyword() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setNickname("Test");
        user.setRole("USER");
        user.setStatus(0);
        user.setCreatedAt(LocalDateTime.now());

        Page<User> userPage = new Page<>(1, 10);
        userPage.setRecords(List.of(user));
        userPage.setTotal(1);

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(userPage);

        Page<UserManageVO> result = adminService.getUserList(1, 10, "test");

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void updateUserStatus_shouldPreventDisablingAdmin() {
        User admin = new User();
        admin.setId(1L);
        admin.setRole("admin");
        admin.setStatus(0);

        when(userMapper.selectById(1L)).thenReturn(admin);

        assertThrows(BusinessException.class, () -> adminService.updateUserStatus(1L, 0));
    }

    @Test
    void updateUserStatus_shouldAllowDisablingNormalUser() {
        User user = new User();
        user.setId(2L);
        user.setRole("USER");
        user.setStatus(0);

        when(userMapper.selectById(2L)).thenReturn(user);

        adminService.updateUserStatus(2L, 1);

        verify(userMapper).updateById(user);
        assertEquals(1, user.getStatus());
    }

    @Test
    void getStatistics_shouldReturnCorrectCounts() {
        when(userMapper.selectCount(isNull())).thenReturn(100L);
        when(redisUtil.getSetMembers(RedisConstants.ONLINE_USERS)).thenReturn(Set.of(1L, 2L, 3L));

        StatisticsVO result = adminService.getStatistics();

        assertNotNull(result);
        assertEquals(100L, result.getTotalUsers());
        assertEquals(3, result.getOnlineUsers());
    }
}
