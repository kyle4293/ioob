package com.ioob.backend.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ioob.backend.global.exception.ErrorCode;
import com.ioob.backend.global.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // 해당 경로에서 필터를 적용하지 않음
        return path.startsWith("/api/auth/register") ||
                path.startsWith("/api/auth/refresh") ||
                path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/logout") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/api/auth/verify");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String tokenValue = jwtUtil.getTokenFromRequest(request);

        if (tokenValue != null) {
            try {
                if (jwtUtil.validateToken(tokenValue)) {
                    String email = jwtUtil.getEmailFromToken(tokenValue);
                    setAuthentication(email, request);
                }
            } catch (ExpiredJwtException e) {
                sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
                return;
            } catch (JwtException e) {
                sendErrorResponse(response, ErrorCode.INVALID_TOKEN);
                return;
            }
        } else {
            sendErrorResponse(response, ErrorCode.TOKEN_NOT_FOUND);
            return;
        }

        chain.doFilter(request, response);
    }

    private void setAuthentication(String email, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void sendErrorResponse(HttpServletResponse res, ErrorCode errorCode) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.failure(errorCode.getMessage());
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.setStatus(errorCode.getStatus().value());
        res.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
    }
}
