package com.ioob.backend.domain.kanban.aop;

import com.ioob.backend.domain.kanban.service.RoleService;
import com.ioob.backend.global.exception.CustomException;
import com.ioob.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ProjectAuthorizationAspect {

    private final RoleService roleService;

    @Pointcut("execution(* com.ioob.backend.domain.kanban.service.BoardService.getAllBoards(..)) && args(projectId,..)")
    public void projectBoardOperations(Long projectId) {}

    @Pointcut("execution(* com.ioob.backend.domain.kanban.service.ProjectService.getProjectById(..)) && args(projectId,..)")
    public void projectOperations(Long projectId) {}

    @Before("projectBoardOperations(projectId) || projectOperations(projectId)")
    public void checkProjectAuthorization(Long projectId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // ROLE_ADMIN이면 모든 접근 허용
        if (isAdmin) {
            return;
        }

        // ROLE_ADMIN이 아닌 경우, 권한 검사
        if (!roleService.isUserInProject(projectId, email)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
    }
}

