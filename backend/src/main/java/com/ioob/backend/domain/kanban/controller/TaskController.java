package com.ioob.backend.domain.kanban.controller;

import com.ioob.backend.domain.kanban.dto.TaskRequestDto;
import com.ioob.backend.domain.kanban.dto.TaskResponseDto;
import com.ioob.backend.domain.kanban.service.TaskService;
import com.ioob.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public List<TaskResponseDto> getAllTasks(@RequestParam Long boardId) {
        return taskService.getAllTasks(boardId);
    }

    @Operation(summary = "작업 생성", description = "새로운 작업을 생성하는 API(TODO, IN_PROGRESS, DONE)")
    @PostMapping
    public TaskResponseDto createTask(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TaskRequestDto taskRequestDto) {
        return taskService.createTask(userDetails.getUser(), taskRequestDto);
    }

    @Operation(summary = "작업 수정", description = "ID를 통해 특정 작업을 수정하는 API(TODO, IN_PROGRESS, DONE)")
    @PutMapping("/{id}")
    public TaskResponseDto updateTask(@PathVariable Long id, @RequestBody TaskRequestDto taskRequestDto) {
        return taskService.updateTask(id, taskRequestDto);
    }

    @Operation(summary = "작업 삭제", description = "ID를 통해 특정 작업을 삭제하는 API")
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
