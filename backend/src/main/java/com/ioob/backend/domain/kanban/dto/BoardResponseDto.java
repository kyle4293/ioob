package com.ioob.backend.domain.kanban.dto;

import com.ioob.backend.domain.kanban.entity.Board;
import com.ioob.backend.domain.kanban.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDto {
    private Long id;
    private String name;
    private Long projectId;
    private Integer boardOrder;
    private List<TaskResponseDto> tasks;

    private BoardResponseDto(Board board) {
        this.id = board.getId();
        this.name = board.getName();
        this.projectId = board.getId();
        this.boardOrder = board.getBoardOrder();
        this.tasks = TaskResponseDto.of(board.getTasks());
    }

    public static BoardResponseDto of(Board board) {
        return new BoardResponseDto(board);
    }
}
