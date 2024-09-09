package com.ioob.backend.dto;

import com.ioob.backend.entity.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserProjectRoleDto {
    private String userName;
    private String userEmail;
    private RoleName role;
}
