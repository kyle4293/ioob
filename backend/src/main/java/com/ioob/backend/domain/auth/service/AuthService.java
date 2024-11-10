package com.ioob.backend.domain.auth.service;

import com.ioob.backend.domain.auth.dto.UserRegisterRequestDto;
import com.ioob.backend.domain.auth.entity.RefreshToken;
import com.ioob.backend.domain.auth.entity.User;
import com.ioob.backend.domain.auth.entity.VerificationToken;
import com.ioob.backend.domain.auth.repository.RefreshTokenRepository;
import com.ioob.backend.domain.auth.repository.UserRepository;
import com.ioob.backend.domain.auth.repository.VerificationTokenRepository;
import com.ioob.backend.domain.kanban.entity.Role;
import com.ioob.backend.global.exception.CustomException;
import com.ioob.backend.global.exception.ErrorCode;
import com.ioob.backend.global.security.JwtUtil;
import com.ioob.backend.global.service.EmailService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    @Transactional
    public void registerNewUser(UserRegisterRequestDto dto) throws MessagingException {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());

        // User 엔티티 생성 시 암호화된 비밀번호를 사용
        User user = User.createUser(dto.getName(),dto.getEmail(),encryptedPassword, Role.ROLE_USER);

        userRepository.save(user);
        VerificationToken verificationToken = new VerificationToken(user);
        verificationTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken.getToken());
    }

    // Access Token 재발급 처리
    public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getRefreshTokenFromRequest(request);
        try {
            log.info("refreshToken="+refreshToken);
            jwtUtil.validateToken(refreshToken);
        } catch (ExpiredJwtException e) {
            // refresh token 만료시 쿠키에서 삭제
            jwtUtil.deleteJwtCookies(response);  // 쿠키 삭제
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }

//        refresh token은 보안성을 위해 이메일 정보를 갖고 있지 않으므로
//        서버에 이메일 정보와 토큰을 함께 저장하고, 토큰 재발급 시에 이를 사용해 검증함.
        RefreshToken refreshTokenRecord = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));

        String email = refreshTokenRecord.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String role = user.getRole().name();
        String newAccessToken = jwtUtil.createAccessToken(email, role);
        jwtUtil.addJwtToCookie(newAccessToken, refreshToken, response);
    }

    // 로그아웃 처리
    public void logout(HttpServletResponse response) {
        jwtUtil.deleteJwtCookies(response);  // 쿠키 삭제
    }
}
