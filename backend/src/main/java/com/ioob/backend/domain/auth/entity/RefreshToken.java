package com.ioob.backend.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity // JPA 사용 시
@Table(name = "refresh_tokens") // 테이블 명 지정
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 ID
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true) // 토큰 값은 고유해야 함
    private String token;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP) // 날짜 타입 지정
    private Date expirationDate;

    // private 생성자
    private RefreshToken(String email, String token, Date expirationDate) {
        this.email = email;
        this.token = token;
        this.expirationDate = expirationDate;
    }

    // 토큰 생성 메서드
    public static RefreshToken createToken(String email, String token, Date expirationDate) {
        return new RefreshToken(email, token, expirationDate);
    }

    // 만료 여부 확인 메서드
    public boolean isExpired() {
        return expirationDate.before(new Date());
    }
}
