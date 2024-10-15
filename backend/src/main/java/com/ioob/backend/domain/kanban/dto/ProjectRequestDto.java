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
public class ProjectRequestDto {

    @Schema(description = "프로젝트 이름", example = "New Project")
    private String name;

    @Schema(description = "프로젝트 설명", example = "This is a new project")
    private String description;
}
