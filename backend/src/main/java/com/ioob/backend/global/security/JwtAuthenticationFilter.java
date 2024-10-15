package com.ioob.backend.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ioob.backend.domain.auth.dto.UserLoginRequestDto;
import com.ioob.backend.domain.auth.entity.RefreshToken;
import com.ioob.backend.domain.auth.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

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
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            log.info("로그인 시도");
            UserLoginRequestDto loginRequest = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestDto.class);
            log.info("Login attempt for user: {}", loginRequest.getEmail());

            return getAuthenticationManager().authenticate( // AuthenticationManager 사용
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (IOException e) {
            log.error(e.getMessage());
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
        log.info("로그인 실패");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
    }
}
