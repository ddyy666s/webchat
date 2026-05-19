package com.chat.chat_backend.modules.user.service;

import com.chat.chat_backend.BaseServiceTest;
import com.chat.chat_backend.common.exception.BusinessException;
import com.chat.chat_backend.common.utils.JwtUtil;
import com.chat.chat_backend.common.utils.RedisUtil;
import com.chat.chat_backend.modules.user.dto.request.LoginRequest;
import com.chat.chat_backend.modules.user.dto.request.RegisterRequest;
import com.chat.chat_backend.modules.user.dto.response.LoginResponse;
import com.chat.chat_backend.modules.user.dto.response.UserInfoResponse;
import com.chat.chat_backend.modules.user.entity.User;
import com.chat.chat_backend.modules.user.mapper.UserMapper;
import com.chat.chat_backend.modules.user.service.impl.UserProfileServiceImpl;
import com.chat.chat_backend.modules.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import com.chat.chat_backend.modules.user.entity.User;
import static org.mockito.Mockito.*;

class UserServiceTest extends BaseServiceTest {

    @Mock private UserMapper userMapper;
    @Mock private JwtUtil jwtUtil;
    @Mock private RedisUtil redisUtil;
    @Mock private UserProfileServiceImpl userProfileService;

    @InjectMocks
    private UserServiceImpl userService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void register_shouldSucceed() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setNickname("New User");
        when(userMapper.selectOne(any())).thenReturn(null);
        assertDoesNotThrow(() -> userService.register(request));
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void register_shouldThrowWhenUsernameExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing");
        request.setPassword("password123");
        request.setNickname("Existing");
        when(userMapper.selectOne(any())).thenReturn(new User());
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.register(request));
        assertEquals(1002, ex.getCode());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void login_shouldSucceed() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("correct");
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword(encoder.encode("correct"));
        user.setNickname("Test");
        user.setRole("user");
        user.setStatus(1);
        when(userMapper.selectOne(any())).thenReturn(user);
        when(jwtUtil.generateToken(1L, "testuser", "user")).thenReturn("jwt-token");
        LoginResponse response = userService.login(request);
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("testuser", response.getUser().getUsername());
        verify(userMapper).updateById(user);
        verify(redisUtil).addToSet(anyString(), anyString());
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nobody");
        when(userMapper.selectOne(any())).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.login(request));
        assertEquals(1001, ex.getCode());
    }

    @Test
    void login_shouldThrowWhenPasswordWrong() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrong");
        User user = new User();
        user.setPassword(encoder.encode("correct"));
        user.setStatus(1);
        when(userMapper.selectOne(any())).thenReturn(user);
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.login(request));
        assertEquals(1003, ex.getCode());
    }

    @Test
    void login_shouldThrowWhenUserDisabled() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("correct");
        User user = new User();
        user.setPassword(encoder.encode("correct"));
        user.setStatus(0);
        when(userMapper.selectOne(any())).thenReturn(user);
        BusinessException ex = assertThrows(BusinessException.class, () -> userService.login(request));
        assertEquals(1004, ex.getCode());
    }

    @Test
    void getUserInfo_shouldReturnUserInfo() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setNickname("Test");
        user.setAvatar("avatar.jpg");
        user.setSignature("hello");
        user.setRole("user");
        when(userMapper.selectById(1L)).thenReturn(user);
        UserInfoResponse response = userService.getUserInfo(1L);
        assertEquals("testuser", response.getUsername());
        assertEquals("Test", response.getNickname());
    }
}
