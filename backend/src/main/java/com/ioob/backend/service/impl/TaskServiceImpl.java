package com.ioob.backend.service.impl;

import com.ioob.backend.dto.TaskRequestDto;
import com.ioob.backend.dto.TaskResponseDto;
import com.ioob.backend.entity.*;
import com.ioob.backend.exception.CustomException;
import com.ioob.backend.exception.ErrorCode;
import com.ioob.backend.repository.BoardRepository;
import com.ioob.backend.repository.TaskRepository;
import com.ioob.backend.service.RoleService;
import com.ioob.backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;
    private final RoleService roleService;


    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getAllTasks(Long boardId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        Long projectId = board.getProject().getId();

        // 프로젝트 권한 확인
        if (!roleService.isUserInProject(projectId, email)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);
        }

        return taskRepository.findByBoardId(boardId).stream()
                .map(TaskResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long projectId = task.getBoard().getProject().getId();

        if (!roleService.isUserInProject(projectId, email)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);
        }

        return new TaskResponseDto(task);
    }

    @Override
    @Transactional
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) {
        Board board = boardRepository.findById(taskRequestDto.getBoardId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long projectId = board.getProject().getId();

        // 프로젝트 내부에 있는지 확인
        if (!roleService.isUserInProject(projectId, email)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);
        }

        Task task = Task.builder()
                .title(taskRequestDto.getTitle())
                .description(taskRequestDto.getDescription())
                .status(Status.valueOf(taskRequestDto.getStatus()))
                .board(board)
                .build();
        task = taskRepository.save(task);
        return new TaskResponseDto(task);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long projectId = task.getBoard().getProject().getId();

        if (!roleService.hasPermission(projectId, RoleName.ROLE_PROJECT_ADMIN, email)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);
        }

        task.setTitle(taskRequestDto.getTitle());
        task.setDescription(taskRequestDto.getDescription());
        task.setStatus(Status.valueOf(taskRequestDto.getStatus()));

        return new TaskResponseDto(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long projectId = task.getBoard().getProject().getId();

        // 관리자 권한 확인
        if (!roleService.hasPermission(projectId, RoleName.ROLE_PROJECT_ADMIN, email)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);
        }

        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getUserTasks(Long userId) {
        List<Task> tasks = taskRepository.findByUserId(userId);

        return tasks.stream()
                .map(TaskResponseDto::new)
                .collect(Collectors.toList());
    }

}
