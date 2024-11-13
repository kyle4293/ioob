package com.ioob.backend.domain.kanban.dto;

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
    private Long projectId;
    private String projectName;
    private Long boardId;
    private String boardName;
    private String createdByName;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private TaskResponseDto(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.projectName = task.getBoard().getProject().getName();
        this.projectId = task.getBoard().getProject().getId();
        this.boardName = task.getBoard().getName();
        this.boardId = task.getBoard().getId();
        this.createdByName = task.getCreatedBy().getName();
        this.assignedToName = task.getAssignedTo() != null ? task.getAssignedTo().getName() : null;
        this.createdAt = task.getCreatedAt();
        this.modifiedAt = task.getModifiedAt();
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
