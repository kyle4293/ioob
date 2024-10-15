package com.ioob.backend.service;

import com.ioob.backend.dto.UserProfileResponseDto;
import com.ioob.backend.dto.UserRegisterRequestDto;
import com.ioob.backend.entity.RoleName;
import com.ioob.backend.entity.User;
import com.ioob.backend.entity.VerificationToken;
import com.ioob.backend.exception.CustomException;
import com.ioob.backend.exception.ErrorCode;
import com.ioob.backend.repository.UserRepository;
import com.ioob.backend.repository.VerificationTokenRepository;
import com.ioob.backend.security.JwtUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailServiceImpl emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    


    @Transactional
    
    public boolean verifyEmail(String token) {
        Optional<VerificationToken> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isPresent()) {
            VerificationToken verificationToken = optionalToken.get();
            User user = verificationToken.getUser();

            if (verificationToken.getExpiryDate().before(new Date())) {
                tokenRepository.delete(verificationToken);
                return false; // 토큰 만료
            }

            user.verified();
            userRepository.save(user);
            tokenRepository.delete(verificationToken);
            return true;
        }

        return false;
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
            return new UserProfileResponseDto(user.getName(), user.getEmail(), user.getRole(), user.isEnabled());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    
    @Transactional(readOnly = true)
    public List<UserProfileResponseDto> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .map(UserProfileResponseDto::new)  // User 객체를 UserProfileResponseDto로 변환
                    .collect(Collectors.toList());
        } catch (Exception e) {
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
