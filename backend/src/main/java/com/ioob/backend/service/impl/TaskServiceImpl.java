package com.ioob.backend.service.impl;

import com.ioob.backend.dto.TaskRequestDto;
import com.ioob.backend.dto.TaskResponseDto;
import com.ioob.backend.entity.Board;
import com.ioob.backend.entity.Status;
import com.ioob.backend.entity.Task;
import com.ioob.backend.entity.User;
import com.ioob.backend.exception.CustomException;
import com.ioob.backend.exception.ErrorCode;
import com.ioob.backend.repository.BoardRepository;
import com.ioob.backend.repository.TaskRepository;
import com.ioob.backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getAllTasks(Long boardId) {
        return taskRepository.findByBoardId(boardId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));
        return convertToDto(task);
    }

    @Override
    @Transactional
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) {
        Board board = boardRepository.findById(taskRequestDto.getBoardId())
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        Task task = Task.builder()
                .title(taskRequestDto.getTitle())
                .description(taskRequestDto.getDescription())
                .status(Status.valueOf(taskRequestDto.getStatus()))  // 문자열 상태를 enum으로 변환
                .board(board)
                .build();
        task = taskRepository.save(task);
        return convertToDto(task);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        task.setTitle(taskRequestDto.getTitle());
        task.setDescription(taskRequestDto.getDescription());
        task.setStatus(Status.valueOf(taskRequestDto.getStatus()));  // 문자열 상태를 enum으로 변환
        return convertToDto(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));
        taskRepository.delete(task);
    }

    // convertToDto 메서드에서 Status enum을 그대로 전달
    private TaskResponseDto convertToDto(Task task) {
        return new TaskResponseDto(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getBoard().getId());
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getUserTasks(Long userId) {
        List<Task> tasks = taskRepository.findByUserId(userId);

        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


}
