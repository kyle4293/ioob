package com.ioob.backend.controller;

import com.ioob.backend.dto.TaskRequestDto;
import com.ioob.backend.dto.TaskResponseDto;
import com.ioob.backend.response.ApiResponse;
import com.ioob.backend.response.ResponseMessage;
import com.ioob.backend.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Task Management", description = "작업(Task) 관련 CRUD API")
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "작업 목록 조회", description = "보드 내 모든 작업을 조회하는 API")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponseDto>>> getAllTasks(@RequestParam Long boardId) {
        List<TaskResponseDto> tasks = taskService.getAllTasks(boardId);
        return ResponseEntity.ok(ApiResponse.success(
                ResponseMessage.TASK_FETCH_SUCCESS.getCode(),
                ResponseMessage.TASK_FETCH_SUCCESS.getMessage(),
                tasks));
    }

    @Operation(summary = "작업 생성", description = "새로운 작업을 생성하는 API(TODO, IN_PROGRESS, DONE)")
    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponseDto>> createTask(@RequestBody TaskRequestDto taskRequestDto) {
        TaskResponseDto createdTask = taskService.createTask(taskRequestDto);
        return ResponseEntity.ok(ApiResponse.success(
                ResponseMessage.TASK_CREATE_SUCCESS.getCode(),
                ResponseMessage.TASK_CREATE_SUCCESS.getMessage(),
                createdTask));
    }

    @Operation(summary = "작업 수정", description = "ID를 통해 특정 작업을 수정하는 API(TODO, IN_PROGRESS, DONE)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponseDto>> updateTask(@PathVariable Long id, @RequestBody TaskRequestDto taskRequestDto) {
        TaskResponseDto updatedTask = taskService.updateTask(id, taskRequestDto);
        return ResponseEntity.ok(ApiResponse.success(
                ResponseMessage.TASK_UPDATE_SUCCESS.getCode(),
                ResponseMessage.TASK_UPDATE_SUCCESS.getMessage(),
                updatedTask));
    }

    @Operation(summary = "작업 삭제", description = "ID를 통해 특정 작업을 삭제하는 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success(
                ResponseMessage.TASK_DELETE_SUCCESS.getCode(),
                ResponseMessage.TASK_DELETE_SUCCESS.getMessage(),
                null));
    }
}
