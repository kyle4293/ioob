package com.ioob.backend.domain.auth.dto;

import com.ioob.backend.domain.auth.entity.User;
import com.ioob.backend.domain.auth.entity.VerificationToken;
import com.ioob.backend.domain.kanban.entity.Role;
import lombok.Getter;

@Getter
public class UserInfoDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean enabled;

    private UserInfoDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.enabled = user.isEnabled();
    }

    public static UserInfoDto from(User user) {
        return new UserInfoDto(user);
    }
}
