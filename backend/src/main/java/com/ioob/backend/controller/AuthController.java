package com.ioob.backend.controller;

import com.ioob.backend.dto.UserLoginRequestDto;
import com.ioob.backend.dto.UserRegisterRequestDto;
import com.ioob.backend.dto.UserProfileResponseDto;
import com.ioob.backend.entity.User;
import com.ioob.backend.exception.CustomException;
import com.ioob.backend.response.ApiResponse;
import com.ioob.backend.security.UserDetailsImpl;
import com.ioob.backend.service.impl.EmailServiceImpl;
import com.ioob.backend.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Authentication", description = "회원 인증 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserServiceImpl userService;
    private final EmailServiceImpl emailService;

    @Operation(
            summary = "회원가입",
            description = "회원가입 할 때 사용하는 API"
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody UserRegisterRequestDto dto) {
        try {
            userService.registerNewUser(dto);
            return ResponseEntity.ok(ApiResponse.success("200", "회원가입 성공", null));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(
            summary = "이메일 인증",
            description = "이메일로 전송된 토큰을 이용해 이메일 인증을 완료하는 API"
    )
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyUser(@RequestParam("token") String token) {
        boolean isVerified = emailService.verifyToken(token);
        if (isVerified) {
            return ResponseEntity.ok(ApiResponse.success("200", "이메일 인증 성공", null));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.failure("400", "잘못된 토큰"));
        }
    }

    @Operation(summary = "로그인", description = "로그인 할 때 사용하는 API")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody UserLoginRequestDto loginRequest) {
        try {
            Map<String, String> tokens = userService.loginUser(loginRequest);
            return ResponseEntity.ok(ApiResponse.success("200", "로그인 성공", tokens));
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getErrorCode().getCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure("400", "로그인 실패"));
        }
    }


    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자가 회원 탈퇴를 요청할 때 사용하는 API")
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            userService.deleteUser(userDetails.getUser());
            return ResponseEntity.ok(ApiResponse.success("200", "회원 탈퇴 성공", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure("400", "회원 탈퇴 실패"));
        }
    }
}
