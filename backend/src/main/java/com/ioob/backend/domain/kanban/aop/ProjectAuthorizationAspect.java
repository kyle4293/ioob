package com.ioob.backend.domain.kanban.aop;

import com.ioob.backend.domain.kanban.repository.ProjectRepository;
import com.ioob.backend.domain.kanban.repository.UserProjectRoleRepository;
import com.ioob.backend.global.exception.CustomException;
import com.ioob.backend.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class ProjectAuthorizationAspect {

    private final UserProjectRoleRepository userProjectRoleRepository;
    private final ProjectRepository projectRepository;

    @Pointcut("execution(* com.ioob.backend.domain.kanban.controller.*.*(..)) && " +
            "!execution(* com.ioob.backend.domain.kanban.controller.ProjectController.getAllProjects(..)) && " +
            "!execution(* com.ioob.backend.domain.kanban.controller.ProjectController.createProject(..))")
    public void authorizedMethods() {}

    @Before("authorizedMethods()")
    public void checkProjectAuthorization() {
        Long projectId = extractProjectIdFromPath();
        if (projectId == null || !projectRepository.existsById(projectId)) {
            throw new CustomException(ErrorCode.PROJECT_NOT_FOUND);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (isAdmin() || userProjectRoleRepository.existsByUserEmailAndProjectId(email, projectId)) {
            return;
        }

        throw new CustomException(ErrorCode.PERMISSION_DENIED);
    }

    private boolean isAdmin() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private Long extractProjectIdFromPath() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String path = request.getRequestURI();
        String[] segments = path.split("/");
        for (int i = 0; i < segments.length; i++) {
            if (segments[i].equals("projects") && i + 1 < segments.length) {
                try {
                    return Long.valueOf(segments[i + 1]);
                } catch (NumberFormatException e) {
                    throw new CustomException(ErrorCode.PROJECT_NOT_FOUND);
                }
            }
        }
        return null;
    }
}
