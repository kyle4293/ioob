package com.ioob.backend.dto;

import com.ioob.backend.entity.Role;
import com.ioob.backend.entity.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponseDto {
    private String name;
    private String email;
    private RoleName role;
    private boolean enabled;
}
