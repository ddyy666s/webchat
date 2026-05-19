package com.chat.chat_backend.modules.user.controller;

import com.chat.chat_backend.BaseControllerTest;
import com.chat.chat_backend.modules.user.dto.request.LoginRequest;
import com.chat.chat_backend.modules.user.dto.request.RegisterRequest;
import com.chat.chat_backend.modules.user.dto.request.UpdateProfileRequest;
import com.chat.chat_backend.modules.user.dto.response.LoginResponse;
import com.chat.chat_backend.modules.user.dto.response.UserInfoResponse;
import com.chat.chat_backend.modules.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends BaseControllerTest {

    @MockBean
    private UserService userService;

    @Test
    void register_shouldReturnSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setNickname("New User");

        performPost("/user/register", request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));

        verify(userService).register(any(RegisterRequest.class));
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        LoginResponse loginResponse = LoginResponse.builder()
                .token("jwt-token")
                .user(UserInfoResponse.builder().id(1L).username("testuser").nickname("Test").build())
                .build();
        when(userService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        performPost("/user/login", request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("jwt-token"));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    void getMe_shouldReturnUserInfo() throws Exception {
        UserInfoResponse userInfo = UserInfoResponse.builder()
                .id(1L).username("testuser").nickname("Test").avatar("avatar.jpg").signature("hi").role("user")
                .build();
        when(userService.getUserInfo(1L)).thenReturn(userInfo);

        performGetWithAuth("/user/me")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(userService).getUserInfo(1L);
    }

    @Test
    void updateProfile_shouldReturnUpdatedUser() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNickname("newname");
        request.setSignature("newsig");

        UserInfoResponse updated = UserInfoResponse.builder()
                .id(1L).username("testuser").nickname("newname").signature("newsig").build();
        when(userService.updateProfile(eq(1L), any(UpdateProfileRequest.class))).thenReturn(updated);

        performPutWithAuth("/user/profile", request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.nickname").value("newname"));

        verify(userService).updateProfile(eq(1L), any(UpdateProfileRequest.class));
    }

    @Test
    void updateAvatar_shouldReturnUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "test-image".getBytes());
        when(userService.updateAvatar(eq(1L), any())).thenReturn("http://oss.url/avatar.png");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .multipart("/user/avatar")
                        .file(file)
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("http://oss.url/avatar.png"));

        verify(userService).updateAvatar(eq(1L), any());
    }

    @Test
    void getUserProfile_shouldReturnProfile() throws Exception {
        UserInfoResponse profile = UserInfoResponse.builder()
                .id(2L).username("other").nickname("Other").build();
        when(userService.getUserProfile(2L)).thenReturn(profile);

        performGetWithAuth("/user/2")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(2));

        verify(userService).getUserProfile(2L);
    }
}
