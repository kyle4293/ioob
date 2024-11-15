package com.ioob.backend.domain.auth.dto;

import com.ioob.backend.domain.auth.entity.User;
import com.ioob.backend.domain.kanban.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean enabled;

    private UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.enabled = user.isEnabled();
    }

    public static UserResponseDto from(User user) {
        return new UserResponseDto(user);
    }
}
