package com.ioob.backend.service;

import com.ioob.backend.dto.UserProjectRoleDto;
import com.ioob.backend.entity.RoleName;
import com.ioob.backend.entity.UserProjectRole;
import com.ioob.backend.exception.CustomException;
import com.ioob.backend.exception.ErrorCode;

import java.util.List;

public interface RoleService {
    void assignRoleToUser(Long projectId, String userEmail, RoleName roleName);
    boolean isUserInProject(Long projectId, String email);
    boolean hasPermission(Long projectId, RoleName requiredRole, String email);
}
