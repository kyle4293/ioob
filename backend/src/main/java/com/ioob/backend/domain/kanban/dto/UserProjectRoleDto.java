package com.ioob.backend.domain.kanban.dto;

import com.ioob.backend.domain.kanban.entity.Role;
import com.ioob.backend.domain.kanban.entity.UserProjectRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProjectRoleDto {
    private String name;
    private String email;
    private Role role;

    private UserProjectRoleDto(UserProjectRole userProjectRole) {
        this.name = userProjectRole.getUser().getName();
        this.email = userProjectRole.getUser().getEmail();
        this.role = userProjectRole.getRole();
    }

    public static UserProjectRoleDto from(UserProjectRole userProjectRole) {
        return new UserProjectRoleDto(userProjectRole);
    }
}
