package com.ioob.backend.domain.kanban.dto;

import com.ioob.backend.domain.kanban.entity.Role;
import com.ioob.backend.domain.kanban.entity.UserProjectRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProjectRoleDto {
    private String userName;
    private String userEmail;
    private Role role;

    private UserProjectRoleDto(UserProjectRole userProjectRole) {
        this.userName = userProjectRole.getUser().getName();
        this.userEmail = userProjectRole.getUser().getEmail();
        this.role = userProjectRole.getRole();
    }

    public static UserProjectRoleDto from(UserProjectRole userProjectRole) {
        return new UserProjectRoleDto(userProjectRole);
    }
}
