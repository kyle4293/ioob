package com.ioob.backend.domain.kanban.dto;

import com.ioob.backend.domain.kanban.entity.Status;
import com.ioob.backend.domain.kanban.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Long boardId;
    private String userName;

    public TaskResponseDto(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.boardId = task.getBoard().getId();
        this.userName = task.getUser().getName();
    }
}
