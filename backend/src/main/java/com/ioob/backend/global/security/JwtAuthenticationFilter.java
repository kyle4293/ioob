package com.ioob.backend.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ioob.backend.domain.auth.dto.UserLoginRequestDto;
import com.ioob.backend.domain.auth.entity.RefreshToken;
import com.ioob.backend.domain.auth.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;


    public JwtAuthenticationFilter(JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            log.info("로그인 시도");
            UserLoginRequestDto loginRequest = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestDto.class);
            log.info("Login attempt for user: {}", loginRequest.getEmail());

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (IOException e) {
            log.error("IOException during authentication: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        log.info("로그인 성공 및 JWT 생성");

        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        String email = userDetails.getUsername();
        String role = userDetails.getUser().getRole().name();

        String accessToken = jwtUtil.createAccessToken(email, role);

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtUtil.REFRESH_TOKEN_EXPIRATION);
        String refreshToken = jwtUtil.createRefreshToken(now, expirationDate);

        refreshTokenRepository.save(RefreshToken.createToken(email, refreshToken, expirationDate));

        jwtUtil.addJwtToCookie(accessToken, refreshToken, response);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{ \"message\": \"Login successful\", \"email\": \"" + email + "\" }");

        log.info("JWT 생성 완료, 사용자: {}", email);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        String errorMessage;

        if (failed instanceof BadCredentialsException) {
            errorMessage = "이메일/비밀번호가 유효하지 않습니다.";
        } else if (failed instanceof UsernameNotFoundException) {
            errorMessage = "계정을 찾을 수 없습니다.";
        } else if (failed instanceof AccountExpiredException) {
            errorMessage = "계정이 만료되었습니다.";
        } else if (failed instanceof CredentialsExpiredException) {
            errorMessage = "비밀번호가 만료되었습니다.";
        } else if (failed instanceof DisabledException) {
            errorMessage = "계정이 비활성화되었습니다. 이메일 인증을 해주세요.";
        } else if (failed instanceof LockedException) {
            errorMessage = "계정이 잠겨 있습니다.";
        } else {
            errorMessage = "인증에 실패하였습니다.";
        }

        log.info("error: {}",errorMessage);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }
}
