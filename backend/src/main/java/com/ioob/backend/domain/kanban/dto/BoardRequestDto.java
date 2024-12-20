package com.ioob.backend.domain.kanban.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDto {
    @Schema(description = "보드 이름", example = "New Board")
    private String name;
    @Schema(description = "프로젝트 ID", example = "1")
    private Long projectId;
}
