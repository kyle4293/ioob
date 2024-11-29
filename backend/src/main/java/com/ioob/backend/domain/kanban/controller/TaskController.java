package com.ioob.backend.domain.kanban.controller;

import com.ioob.backend.domain.kanban.dto.CommentRequestDto;
import com.ioob.backend.domain.kanban.dto.CommentResponseDto;
import com.ioob.backend.domain.kanban.dto.TaskRequestDto;
import com.ioob.backend.domain.kanban.dto.TaskResponseDto;
import com.ioob.backend.domain.kanban.service.TaskService;
import com.ioob.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Task Management", description = "작업(Task) 관련 CRUD API")
@RestController
@RequestMapping("/api/projects/{projectId}/boards/{boardId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "작업 생성", description = "새로운 작업을 생성하는 API(TODO, IN_PROGRESS, DONE)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TaskResponseDto createTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                      @PathVariable Long projectId,
                                      @PathVariable Long boardId,
                                      @ModelAttribute TaskRequestDto taskRequestDto) {
        return taskService.createTask(userDetails.getUser(), projectId, boardId, taskRequestDto);
    }

    @Operation(summary = "작업 상세 조회", description = "작업 상세 화면을 조회하는 API")
    @GetMapping("/{taskId}")
    public TaskResponseDto getTaskById(@PathVariable  Long taskId) {
        return taskService.getTaskById(taskId);
    }

    @Operation(summary = "보드 내 작업 조회", description = "보드 내 작업을 조회하는 API")
    @GetMapping
    public List<TaskResponseDto> getTasksByBoardId(@PathVariable  Long boardId) {
        return taskService.getTasksByBoardId(boardId);
    }

    @Operation(summary = "작업 수정", description = "ID를 통해 특정 작업을 수정하는 API(TODO, IN_PROGRESS, DONE)")
    @PutMapping("/{taskId}")
    public TaskResponseDto updateTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                      @PathVariable  Long projectId,
                                      @PathVariable  Long taskId,
                                      @RequestBody TaskRequestDto taskRequestDto) {
        return taskService.updateTask(userDetails.getUser(), projectId, taskId, taskRequestDto);
    }

    @Operation(summary = "작업 수정", description = "ID를 통해 특정 작업을 수정하는 API(TODO, IN_PROGRESS, DONE)")
    @PutMapping("/{taskId}/move")
    public TaskResponseDto moveTaskToBoard(@PathVariable  Long taskId,
                                           @PathVariable  Long boardId) {
        return taskService.moveTaskToBoard(taskId, boardId);
    }

    @Operation(summary = "작업 삭제", description = "ID를 통해 특정 작업을 삭제하는 API")
    @DeleteMapping("/{taskId}")
    public void deleteTask(@AuthenticationPrincipal UserDetailsImpl userDetails,
                           @PathVariable  Long projectId,
                           @PathVariable  Long taskId) {
        taskService.deleteTask(userDetails.getUser(), projectId, taskId);
    }

    @PostMapping("/{taskId}/comments")
    public CommentResponseDto addComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @PathVariable  Long taskId,
                                         @RequestBody CommentRequestDto dto) {
        return taskService.addComment(userDetails.getUser(),taskId, dto);
    }

    @GetMapping("/{taskId}/comments")
    public List<CommentResponseDto> getComments(@PathVariable Long taskId) {
        return taskService.getCommentsByTaskId(taskId);
    }

    @PutMapping("/{taskId}/comments/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long commentId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody CommentRequestDto dto) {
        return taskService.updateComment(userDetails.getUser(), commentId, dto);
    }

    @DeleteMapping("/{taskId}/comments/{commentId}")
    public void deleteComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                              @PathVariable Long commentId) {
        taskService.deleteComment(userDetails.getUser(), commentId);
    }
}
