package com.ioob.backend.domain.kanban.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BoardOrderDto {
    private Long boardId;
    private Integer newOrder;
}
