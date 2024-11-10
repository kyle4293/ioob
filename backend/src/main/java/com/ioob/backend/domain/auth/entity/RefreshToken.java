package com.ioob.backend.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity // JPA 사용 시
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;

    private RefreshToken(String email, String token, Date expirationDate) {
        this.email = email;
        this.token = token;
        this.expirationDate = expirationDate;
    }

    public static RefreshToken createToken(String email, String token, Date expirationDate) {
        return new RefreshToken(email, token, expirationDate);
    }

}
