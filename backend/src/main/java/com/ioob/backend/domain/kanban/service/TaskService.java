package com.ioob.backend.domain.kanban.service;

import com.ioob.backend.domain.auth.entity.User;
import com.ioob.backend.domain.kanban.dto.CommentRequestDto;
import com.ioob.backend.domain.kanban.dto.CommentResponseDto;
import com.ioob.backend.domain.kanban.dto.TaskRequestDto;
import com.ioob.backend.domain.kanban.dto.TaskResponseDto;
import com.ioob.backend.domain.kanban.entity.Board;
import com.ioob.backend.domain.kanban.entity.Comment;
import com.ioob.backend.domain.kanban.entity.Status;
import com.ioob.backend.domain.kanban.entity.Task;
import com.ioob.backend.domain.kanban.repository.BoardRepository;
import com.ioob.backend.domain.kanban.repository.CommentRepository;
import com.ioob.backend.domain.kanban.repository.TaskRepository;
import com.ioob.backend.global.exception.CustomException;
import com.ioob.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public TaskResponseDto createTask(User user, TaskRequestDto taskRequestDto) {
        Board board = boardRepository.findById(taskRequestDto.getBoardId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        Task task = Task.builder()
                .title(taskRequestDto.getTitle())
                .description(taskRequestDto.getDescription())
                .status(Status.valueOf(taskRequestDto.getStatus()))
                .user(user)
                .board(board)
                .build();
        task = taskRepository.save(task);
        return TaskResponseDto.of(task);
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
    public TaskResponseDto updateTask(User user, Long id, TaskRequestDto taskRequestDto) {
        Task task = findTaskById(id);
        checkPermission(user, task.getUser().getId());

        task.setTitle(taskRequestDto.getTitle());
        task.setDescription(taskRequestDto.getDescription());
        task.setStatus(Status.valueOf(taskRequestDto.getStatus()));
        return TaskResponseDto.of(task);
    }

    @Transactional
    public void deleteTask(User user, Long id) {
        Task task = findTaskById(id);
        checkPermission(user, task.getUser().getId());

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

        return CommentResponseDto.from(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId).stream()
                .map(CommentResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDto updateComment(User user, Long commentId, CommentRequestDto dto) {
        Comment comment = findCommentById(commentId);
        checkPermission(user, comment.getUser().getId());

        comment.setContent(dto.getContent());
        return CommentResponseDto.from(comment);
    }

    @Transactional
    public void deleteComment(User user, Long commentId) {
        Comment comment = findCommentById(commentId);
        checkPermission(user, comment.getUser().getId());

        commentRepository.delete(comment);
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));
    }

    private void checkPermission(User user, Long checkedId) {
        if (!checkedId.equals(user.getId())) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
    }
}
