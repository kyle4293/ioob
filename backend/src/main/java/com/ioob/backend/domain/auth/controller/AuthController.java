package com.ioob.backend.domain.auth.controller;

import com.ioob.backend.domain.auth.dto.UserLoginRequestDto;
import com.ioob.backend.domain.auth.dto.UserRegisterRequestDto;
import com.ioob.backend.domain.auth.service.AuthService;
import com.ioob.backend.domain.auth.service.UserService;
import com.ioob.backend.global.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Tag(name = "Authentication", description = "회원 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;
    private final AuthService authService;

    @Operation(summary = "회원가입", description = "회원가입 할 때 사용하는 API")
    @PostMapping("/register")
    public void register(@RequestBody UserRegisterRequestDto dto) throws MessagingException {
        authService.registerNewUser(dto);
    }

    @Operation(summary = "이메일 인증", description = "이메일로 전송된 토큰을 이용해 이메일 인증을 완료하는 API")
    @GetMapping("/verify")
    public RedirectView verifyUser(@RequestParam("token") String token) {
        userService.verifyEmail(token);
        return new RedirectView("http://localhost:3000");
    }

    @Operation(summary = "AccessToken 재발급", description = "Access token 만료시, Refresh token을 이용해 재발급을 요청하는 API")
    @PostMapping("/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        authService.reissueAccessToken(request, response);
    }

    @Operation(summary = "로그인", description = "로그인 시 사용하는 API")
    @PostMapping("/login")
    public void login(@RequestBody UserLoginRequestDto dto) {
    }

    @Operation(summary = "로그아웃", description = "로그아웃 시 사용하는 API")
    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        authService.logout(response);
    }
}
