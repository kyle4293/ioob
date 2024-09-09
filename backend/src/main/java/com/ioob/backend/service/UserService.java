package com.ioob.backend.service;

import com.ioob.backend.dto.UserProfileResponseDto;
import com.ioob.backend.dto.UserRegisterRequestDto;
import com.ioob.backend.entity.User;
import com.ioob.backend.dto.UserLoginRequestDto;
import com.ioob.backend.dto.RefreshTokenRequest;

import java.util.List;
import java.util.Map;

public interface UserService {
    void registerNewUser(UserRegisterRequestDto dto) throws Exception;
    Map<String, String> loginUser(UserLoginRequestDto loginRequest) throws Exception;
    boolean verifyEmail(String token);
    void deleteUser(User user);
    UserProfileResponseDto getUserProfile(User user);

    List<UserProfileResponseDto> getAllUsers();  // 모든 사용자 조회 (관리자 전용)
    void deleteUserById(Long id);  // 사용자 삭제 (관리자 전용)
}
