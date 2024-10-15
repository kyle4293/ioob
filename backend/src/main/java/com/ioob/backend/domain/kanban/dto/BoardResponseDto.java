package com.ioob.backend.dto;

import com.ioob.backend.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDto {
    private Long id;
    private String name;
    private Long projectId;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.name = board.getName();
        this.projectId = board.getId();
    }
}