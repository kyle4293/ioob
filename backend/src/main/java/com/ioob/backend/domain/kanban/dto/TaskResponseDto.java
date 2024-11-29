package com.ioob.backend.domain.kanban.dto;

import com.ioob.backend.domain.auth.dto.UserResponseDto;
import com.ioob.backend.domain.kanban.entity.Status;
import com.ioob.backend.domain.kanban.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private ProjectResponseDto project;
    private BoardResponseDto board;
    private UserResponseDto createdBy;
    private UserResponseDto assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<TaskFileResponseDto> files;


    private TaskResponseDto(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.project = ProjectResponseDto.of(task.getBoard().getProject());
        this.board = BoardResponseDto.of(task.getBoard());
        this.createdBy = UserResponseDto.from(task.getCreatedBy());
        this.assignedTo = task.getAssignedTo() != null ? UserResponseDto.from(task.getAssignedTo()) : null;
        this.createdAt = task.getCreatedAt();
        this.modifiedAt = task.getModifiedAt();
        this.files = TaskFileResponseDto.of(task.getFiles());
    }

    public static TaskResponseDto of(Task task) {
        return new TaskResponseDto(task);
    }

    public static List<TaskResponseDto> of(List<Task> tasks) {
        return tasks != null
                ? tasks.stream().map(TaskResponseDto::of).collect(Collectors.toList())
                : Collections.emptyList();
    }
}
