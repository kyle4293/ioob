package com.ioob.backend.controller;

import com.ioob.backend.dto.*;
import com.ioob.backend.response.ResponseMessage;
import com.ioob.backend.service.impl.EmailServiceImpl;
import com.ioob.backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private EmailServiceImpl emailService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegisterRequestDto userRegisterRequestDto;
    private UserLoginRequestDto userLoginRequestDto;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        userRegisterRequestDto = new UserRegisterRequestDto();
        userRegisterRequestDto.setName("testUser");
        userRegisterRequestDto.setEmail("test@example.com");
        userRegisterRequestDto.setPassword("password");
        userLoginRequestDto = new UserLoginRequestDto("test@example.com", "password");
    }

    @Test
    @DisplayName("회원 가입 성공")
    void register_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(ResponseMessage.USER_REGISTERED_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResponseMessage.USER_REGISTERED_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void verifyUser_ShouldReturnSuccess() throws Exception {
        when(emailService.verifyToken("valid-token")).thenReturn(true);

        mockMvc.perform(get("/api/auth/verify")
                        .param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(ResponseMessage.EMAIL_VERIFIED_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResponseMessage.EMAIL_VERIFIED_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_ShouldReturnTokens() throws Exception {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", "access-token");
        tokens.put("refreshToken", "refresh-token");

        when(userService.loginUser(any(UserLoginRequestDto.class))).thenReturn(tokens);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(ResponseMessage.LOGIN_SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(ResponseMessage.LOGIN_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
    }


}
