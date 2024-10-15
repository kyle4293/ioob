package com.ioob.backend.domain.auth.service;

import com.ioob.backend.domain.auth.dto.UserInfoDto;
import com.ioob.backend.domain.auth.dto.UserProfileResponseDto;
import com.ioob.backend.domain.auth.entity.User;
import com.ioob.backend.domain.auth.entity.VerificationToken;
import com.ioob.backend.domain.auth.repository.UserRepository;
import com.ioob.backend.domain.auth.repository.VerificationTokenRepository;
import com.ioob.backend.global.exception.CustomException;
import com.ioob.backend.global.exception.ErrorCode;
import com.ioob.backend.global.security.JwtUtil;
import com.ioob.backend.global.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public boolean verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        Calendar cal = Calendar.getInstance();
        if (verificationToken.getExpiryDate().before(cal.getTime())) {
            tokenRepository.delete(verificationToken); // 인증된 토큰 삭제
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);  // 토큰이 만료되었을 때 CustomException 발생
        }

        // 토큰이 유효하면 사용자 계정을 활성화
        verificationToken.getUser().verified();
        tokenRepository.delete(verificationToken); // 인증된 토큰 삭제
        return true;
    }

    @Transactional
    public void deleteUser(User user) {
//        user.setEnabled(false); // 계정 비활성화 (soft delete)
//        userRepository.save(user);

        userRepository.delete(user);  // 사용자 완전 삭제
    }
    
    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(User user) {
        try {
            return UserProfileResponseDto.from(user);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public List<UserInfoDto> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .map(UserInfoDto::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.FETCH_USERS_FAILED); // 사용자 목록 조회 실패 시 예외 처리
        }
    }

    @Transactional
    public void deleteUserById(Long id) {
        try {
            if (!userRepository.existsById(id)) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);  // 사용자 존재 여부 확인
            }
            userRepository.deleteById(id);
        } catch (CustomException e) {
            throw e; // 이미 처리된 예외
        } catch (Exception e) {
            throw new CustomException(ErrorCode.DELETE_USER_FAILED); // 사용자 삭제 실패
        }
    }
}
