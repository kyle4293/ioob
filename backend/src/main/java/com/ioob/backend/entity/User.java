package com.ioob.backend.entity;

import com.ioob.backend.dto.UserRegisterRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<UserProjectRole> userProjectRoles;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private VerificationToken verificationToken;

    private boolean enabled = false; // 이메일 인증 여부

    @Enumerated(EnumType.STRING)
    private RoleName role;  // 전역 역할 (ROLE_ADMIN, ROLE_USER 등)

    @Builder
    public User(String name, String email, String password, boolean enabled, RoleName role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
    }

    public void verified() {
        this.enabled = true;
    }
}
