package com.ioob.backend.domain.kanban.controller;

import com.ioob.backend.domain.auth.entity.User;
import com.ioob.backend.domain.kanban.dto.CommentRequestDto;
import com.ioob.backend.domain.kanban.dto.CommentResponseDto;
import com.ioob.backend.domain.kanban.dto.TaskRequestDto;
import com.ioob.backend.domain.kanban.dto.TaskResponseDto;
import com.ioob.backend.domain.kanban.entity.Comment;
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

    @Operation(summary = "사용자의 테스크 목록 조회", description = "로그인한 사용자가 할당된 모든 테스크 목록을 조회하는 API")
    @GetMapping("/my-tasks")
    public List<TaskResponseDto> getUserTasks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return taskService.getUserTasks(userDetails.getUserId());
    }

    @Operation(summary = "작업 목록 조회", description = "보드 내 모든 작업을 조회하는 API")
    @GetMapping
    public List<TaskResponseDto> getAllTasks(@RequestParam Long boardId) {
        return taskService.getAllTasks(boardId);
    }

    @Operation(summary = "작업 상세 조회", description = "작업 상세 화면을 조회하는 API")
    @GetMapping("/{taskId}")
    public TaskResponseDto getTaskById(@PathVariable Long taskId) {
        return taskService.getTaskById(taskId);
    }

    @Operation(summary = "작업 생성", description = "새로운 작업을 생성하는 API(TODO, IN_PROGRESS, DONE)")
    @PostMapping
    public TaskResponseDto createTask(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody TaskRequestDto taskRequestDto) {
        return taskService.createTask(userDetails.getUser(), taskRequestDto);
    }

    @Operation(summary = "작업 수정", description = "ID를 통해 특정 작업을 수정하는 API(TODO, IN_PROGRESS, DONE)")
    @PutMapping("/{taskId}")
    public TaskResponseDto updateTask(@PathVariable Long taskId, @RequestBody TaskRequestDto taskRequestDto) {
        return taskService.updateTask(taskId, taskRequestDto);
    }

    @Operation(summary = "작업 삭제", description = "ID를 통해 특정 작업을 삭제하는 API")
    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
    }

    @PostMapping("/{taskId}/comments")
    public CommentResponseDto addComment(@PathVariable Long taskId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @RequestBody CommentRequestDto dto) {
        User user = userDetails.getUser();
        return taskService.addComment(taskId, user, dto);
    }

    @GetMapping("/{taskId}/comments")
    public List<CommentResponseDto> getComments(@PathVariable Long taskId) {
        return taskService.getCommentsByTaskId(taskId);
    }

    @PutMapping("/{taskId}/comments/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long taskId, @PathVariable Long commentId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody CommentRequestDto dto) {
        User user = userDetails.getUser();
        return taskService.updateComment(commentId, user, dto);
    }

    @DeleteMapping("/{taskId}/comments/{commentId}")
    public void deleteComment(@PathVariable Long taskId, @PathVariable Long commentId,
                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        taskService.deleteComment(commentId, user);
    }
}
