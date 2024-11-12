package com.ioob.backend.domain.kanban.dto;

import com.ioob.backend.domain.kanban.entity.Board;
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
public class BoardResponseDto {
    private Long id;
    private String name;
    private Long projectId;
    private List<TaskResponseDto> tasks;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.name = board.getName();
        this.projectId = board.getId();
        this.tasks = TaskResponseDto.of(board.getTasks());
    }
}
