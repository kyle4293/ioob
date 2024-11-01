package com.ioob.backend.domain.kanban.aop;

import com.ioob.backend.global.exception.CustomException;
import com.ioob.backend.global.exception.ErrorCode;
import com.ioob.backend.domain.kanban.service.RoleService;
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

    // 특정 보드에 대한 권한 확인
    @Pointcut("execution(* com.ioob.backend.domain.kanban.service.BoardService.getAllBoards(..)) && args(projectId,..)")
    public void projectBoardOperations(Long projectId) {}

    // 특정 프로젝트에 대한 권한 확인 (단일 프로젝트에 대한 작업)
    @Pointcut("execution(* com.ioob.backend.domain.kanban.service.ProjectService.getProjectById(..)) && args(projectId,..)")
    public void projectOperations(Long projectId) {}

    // 프로젝트와 보드에 대한 권한 확인
    @Before("projectBoardOperations(projectId) || projectOperations(projectId)")
    public void checkProjectAuthorization(Long projectId) {
        // 현재 사용자의 이메일과 권한 가져오기
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // ROLE_ADMIN이면 모든 접근 허용
        if (isAdmin) {
            return;
        }

        // ROLE_ADMIN이 아닌 경우, 권한 검사를 수행
        if (!roleService.isUserInProject(projectId, email)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
    }
}

