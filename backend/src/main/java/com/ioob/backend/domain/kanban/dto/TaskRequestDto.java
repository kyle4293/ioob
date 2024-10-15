package com.ioob.backend.dto;

import com.ioob.backend.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto {
    @Schema(description = "작업 제목", example = "New Task")
    private String title;
    @Schema(description = "작업 설명", example = "This is a new Task")
    private String description;
    @Schema(description = "작업 상태", example = "TODO, IN_PROGRESS, DONE")
    private String status; // "TODO", "IN_PROGRESS", "DONE"
    @Schema(description = "작업이 속한 보드 ID", example = "1")
    private Long boardId; // 해당 작업이 속한 보드 ID
}
