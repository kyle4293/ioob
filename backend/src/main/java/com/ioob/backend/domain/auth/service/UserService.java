package com.ioob.backend.domain.auth.service;

import com.ioob.backend.domain.auth.dto.UserProfileResponseDto;
import com.ioob.backend.domain.auth.entity.User;
import com.ioob.backend.domain.auth.entity.VerificationToken;
import com.ioob.backend.domain.auth.repository.UserRepository;
import com.ioob.backend.domain.auth.repository.VerificationTokenRepository;
import com.ioob.backend.domain.kanban.dto.ProjectResponseDto;
import com.ioob.backend.domain.kanban.dto.TaskResponseDto;
import com.ioob.backend.domain.kanban.entity.Task;
import com.ioob.backend.domain.kanban.entity.UserProjectRole;
import com.ioob.backend.domain.kanban.repository.TaskRepository;
import com.ioob.backend.domain.kanban.repository.UserProjectRoleRepository;
import com.ioob.backend.global.exception.CustomException;
import com.ioob.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserProjectRoleRepository userProjectRoleRepository;
    private final VerificationTokenRepository tokenRepository;

    public boolean verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        Calendar cal = Calendar.getInstance();
        if (verificationToken.getExpiryDate().before(cal.getTime())) {
            tokenRepository.delete(verificationToken);
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }

        // 토큰이 유효하면 사용자 계정을 활성화
        verificationToken.getUser().verified();
        tokenRepository.delete(verificationToken);
        return true;
    }

    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }
    
    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(User user) {
        return UserProfileResponseDto.from(user);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getUserProjects(Long userId) {
        List<UserProjectRole> userProjectRoles = userProjectRoleRepository.findByUserId(userId);
        return userProjectRoles.stream()
                .map(userProjectRole -> ProjectResponseDto.of(userProjectRole.getProject()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getUserTasks(Long userId) {
        List<Task> tasks = taskRepository.findByAssignedToId(userId);
        return tasks.stream()
                .map(TaskResponseDto::of)
                .collect(Collectors.toList());
    }
}
