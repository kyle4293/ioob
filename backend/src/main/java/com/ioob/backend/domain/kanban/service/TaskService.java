package com.ioob.backend.domain.kanban.service;

import com.ioob.backend.domain.auth.entity.User;
import com.ioob.backend.domain.kanban.dto.CommentRequestDto;
import com.ioob.backend.domain.kanban.dto.CommentResponseDto;
import com.ioob.backend.domain.kanban.dto.TaskRequestDto;
import com.ioob.backend.domain.kanban.dto.TaskResponseDto;
import com.ioob.backend.domain.kanban.entity.*;
import com.ioob.backend.domain.kanban.repository.*;
import com.ioob.backend.global.exception.CustomException;
import com.ioob.backend.global.exception.ErrorCode;
import com.ioob.backend.global.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UserProjectRoleRepository userProjectRoleRepository;
    private final FileStorageService fileStorageService;
    private final TaskFileRepository taskFileRepository;

    @Transactional
    public TaskResponseDto createTask(User user, Long projectId, Long boardId, TaskRequestDto taskRequestDto) {
        UserProjectRole projectRole = checkAssignedTo(projectId, taskRequestDto);

        Task task = Task.builder()
                .title(taskRequestDto.getTitle())
                .description(taskRequestDto.getDescription())
                .status(Status.valueOf(taskRequestDto.getStatus()))
                .createdBy(user)
                .assignedTo(projectRole != null ? projectRole.getUser() : null)
                .board(findBoardById(boardId))
                .build();

        if (taskRequestDto.getFiles() != null) {
            for (MultipartFile file : taskRequestDto.getFiles()) {
                String fileUrl = fileStorageService.saveFile(file);
                TaskFile taskFile = TaskFile.builder()
                        .fileName(file.getOriginalFilename())
                        .fileUrl(fileUrl)
                        .task(task)
                        .build();
                taskFileRepository.save(taskFile);
            }
        }

        return TaskResponseDto.of(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getTasksByBoardId(Long boardId) {
        return taskRepository.findByBoardId(boardId).stream()
                .map(TaskResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = findTaskById(id);
        return TaskResponseDto.of(task);
    }

    @Transactional
    public TaskResponseDto updateTask(User user, Long projectId, Long taskId, TaskRequestDto taskRequestDto) {
        Task task = findTaskById(taskId);
        checkTaskPermission(user, projectId, task);

        UserProjectRole projectRole = checkAssignedTo(projectId, taskRequestDto);

        Task updatedTask = Task.builder()
                .id(task.getId())
                .title(taskRequestDto.getTitle())
                .description(taskRequestDto.getDescription())
                .status(Status.valueOf(taskRequestDto.getStatus()))
                .createdBy(task.getCreatedBy())
                .assignedTo(projectRole != null ? projectRole.getUser() : null)
                .board(findBoardById(taskRequestDto.getBoardId()))
                .comments(task.getComments())
                .build();

        return TaskResponseDto.of(taskRepository.save(updatedTask));
    }

    @Transactional
    public TaskResponseDto moveTaskToBoard(Long taskId, Long newBoardId) {
        Task task = findTaskById(taskId);
        Board newBoard = findBoardById(newBoardId);
        task.setBoard(newBoard);
        return TaskResponseDto.of(taskRepository.save(task));
    }

    private UserProjectRole checkAssignedTo(Long projectId, TaskRequestDto taskRequestDto) {
        UserProjectRole projectRole = null;
        String assignedToEmail = taskRequestDto.getAssignedToEmail();

        if (assignedToEmail != null && !assignedToEmail.trim().isEmpty()) {
            projectRole = userProjectRoleRepository.findByUserEmailAndProjectId(taskRequestDto.getAssignedToEmail(), projectId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_IN_PROJECT));
        }
        return projectRole;
    }

    @Transactional
    public void deleteTask(User user, Long projectId, Long taskId) {
        Task task = findTaskById(taskId);
        checkTaskPermission(user, projectId, task);

        taskRepository.delete(task);
    }

    @Transactional
    public CommentResponseDto addComment(User user, Long taskId, CommentRequestDto dto) {
        Task task = findTaskById(taskId);

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .task(task)
                .user(user)
                .build();

        return CommentResponseDto.of(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId).stream()
                .map(CommentResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDto updateComment(User user, Long commentId, CommentRequestDto dto) {
        Comment comment = findCommentById(commentId);
        checkCommentPermission(user, comment);

        comment.setContent(dto.getContent());
        return CommentResponseDto.of(comment);
    }

    @Transactional
    public void deleteComment(User user, Long commentId) {
        Comment comment = findCommentById(commentId);
        checkCommentPermission(user, comment);

        commentRepository.delete(comment);
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findByIdWithFiles(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
    }

    private void checkTaskPermission(User user, Long projectId, Task task) {
        if (user.isAdmin()) {
            return;
        }

        UserProjectRole projectRole  = userProjectRoleRepository.findByUserAndProjectId(user, projectId)
                .orElseThrow(()->new CustomException(ErrorCode.PERMISSION_DENIED));
        if (projectRole.getRole().equals(Role.ROLE_PROJECT_ADMIN)) {
            return;
        }

        boolean isCreator = task.getCreatedBy().getId().equals(user.getId());
        boolean isAssignee = task.getAssignedTo() != null && task.getAssignedTo().getId().equals(user.getId());

        if (!(isCreator || isAssignee)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
    }

    private void checkCommentPermission(User user, Comment comment) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
    }
}
