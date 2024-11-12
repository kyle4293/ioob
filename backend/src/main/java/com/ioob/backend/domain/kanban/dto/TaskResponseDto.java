package com.ioob.backend.domain.kanban.dto;

import com.ioob.backend.domain.kanban.entity.Status;
import com.ioob.backend.domain.kanban.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String userName;

    private TaskResponseDto(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.projectName = task.getBoard().getProject().getName();
        this.projectId = task.getBoard().getProject().getId();
        this.boardId = task.getBoard().getId();
        this.userName = task.getUser().getName();
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
