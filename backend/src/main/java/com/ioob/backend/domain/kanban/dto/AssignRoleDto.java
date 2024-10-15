package com.ioob.backend.domain.kanban.dto;

import com.ioob.backend.domain.kanban.entity.Role;
import lombok.Getter;

@Getter
public class AssignRoleDto {
    private String userEmail;
    private Role role;
}
