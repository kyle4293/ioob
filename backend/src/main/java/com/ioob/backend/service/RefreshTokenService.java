package com.ioob.backend.service;

import com.ioob.backend.entity.User;

import java.util.Map;

public interface RefreshTokenService {
    Map<String, String> createTokens(User user);
    Map<String, String> refreshTokens(String refreshToken) throws Exception;
}
