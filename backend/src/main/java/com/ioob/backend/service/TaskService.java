package com.ioob.backend.service;

import com.ioob.backend.dto.TaskRequestDto;
import com.ioob.backend.dto.TaskResponseDto;

import java.util.List;

public interface TaskService {
    List<TaskResponseDto> getAllTasks(Long boardId);
    TaskResponseDto getTaskById(Long id);
    TaskResponseDto createTask(TaskRequestDto taskRequestDto);
    TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto);
    void deleteTask(Long id);
    List<TaskResponseDto> getUserTasks(Long userId);
}
