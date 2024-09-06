package com.ioob.backend.service.impl;

import com.ioob.backend.dto.UserProfileResponseDto;
import com.ioob.backend.dto.UserRegisterRequestDto;
import com.ioob.backend.entity.RoleName;
import com.ioob.backend.entity.User;
import com.ioob.backend.entity.VerificationToken;
import com.ioob.backend.exception.CustomException;
import com.ioob.backend.exception.ErrorCode;
import com.ioob.backend.repository.UserRepository;
import com.ioob.backend.repository.VerificationTokenRepository;
import com.ioob.backend.dto.UserLoginRequestDto;
import com.ioob.backend.dto.RefreshTokenRequest;
import com.ioob.backend.security.JwtUtil;
import com.ioob.backend.security.UserDetailsImpl;
import com.ioob.backend.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailServiceImpl emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void registerNewUser(UserRegisterRequestDto dto) throws MessagingException {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());

        // User 엔티티 생성 시 암호화된 비밀번호를 사용
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(encryptedPassword)  // 암호화된 비밀번호 저장
                .enabled(false)  // 이메일 인증 대기 상태
                .role(RoleName.ROLE_USER)
                .build();
        
        userRepository.save(user);
        VerificationToken verificationToken = new VerificationToken(user);
        tokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken.getToken());
    }


    @Override
    public Map<String, String> loginUser(UserLoginRequestDto loginRequest) {
        try {
            // 이메일로 사용자 조회
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)); // 사용자가 없는 경우

            // 이메일 인증 여부 확인
            if (!user.isEnabled()) {
                throw new CustomException(ErrorCode.EMAIL_VERIFICATION_REQUIRED);  // 이메일 인증 미완료
            }

            // 비밀번호 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            return tokens;
        } catch (BadCredentialsException e) {
            // 비밀번호 불일치
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        } catch (CustomException e) {
            throw e; // 이미 처리된 예외를 다시 던짐
        } catch (Exception e) {
            System.out.println("e = " + e);
            throw new CustomException(ErrorCode.LOGIN_FAILURE); // 기타 예외 처리
        }
    }


    @Transactional
    @Override
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

    @Override
    @Transactional
    public void deleteUser(User user) {
//        user.setEnabled(false); // 계정 비활성화 (soft delete)
//        userRepository.save(user);

        userRepository.delete(user);  // 사용자 완전 삭제
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(User user) {
        return new UserProfileResponseDto(user.getName(), user.getEmail(), user.getRole() ,user.isEnabled());
    }


    @Override
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FETCH_USERS_FAILED); // 사용자 목록 조회 실패
        }
    }


    @Override
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
