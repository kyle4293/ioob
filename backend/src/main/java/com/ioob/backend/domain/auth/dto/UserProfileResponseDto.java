package com.ioob.backend.domain.auth.dto;

import com.ioob.backend.domain.auth.entity.User;
import com.ioob.backend.domain.kanban.entity.Role;
import lombok.Getter;

@Getter
public class UserProfileResponseDto {
    private String name;
    private String email;
    private Role role;
    private boolean enabled;

    private UserProfileResponseDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.enabled = user.isEnabled();
    }

    public static UserProfileResponseDto from(User user) {
        return new UserProfileResponseDto(user);
    }
}
