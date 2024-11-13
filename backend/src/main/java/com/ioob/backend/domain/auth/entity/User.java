package com.ioob.backend.domain.auth.entity;

import com.ioob.backend.domain.kanban.entity.Role;
import com.ioob.backend.domain.kanban.entity.Task;
import com.ioob.backend.domain.kanban.entity.UserProjectRole;
import com.ioob.backend.global.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
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

    private boolean enabled = false; // 이메일 인증 여부

    @Enumerated(EnumType.STRING)
    private Role role;  // 전역 역할 (ROLE_ADMIN, ROLE_USER)

    private User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static User createUser(String name, String email, String password, Role role) {
        return new User(name,email,password,role);
    }

    public void verified() {
        this.enabled = true;
    }

    public boolean isAdmin() {
        return this.role==Role.ROLE_ADMIN;
    }
}
