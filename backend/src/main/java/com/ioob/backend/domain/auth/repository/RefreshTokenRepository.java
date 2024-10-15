package com.ioob.backend.domain.auth.repository;

import com.ioob.backend.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    // 토큰을 통한 RefreshToken 조회
    Optional<RefreshToken> findByToken(String token);

    // 이메일로 기존 토큰 삭제
    void deleteByEmail(String email);
}
