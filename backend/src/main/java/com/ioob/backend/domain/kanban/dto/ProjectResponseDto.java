package com.ioob.backend.domain.kanban.dto;

import com.ioob.backend.domain.kanban.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public ProjectResponseDto(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.createdAt = project.getCreatedAt();
        this.modifiedAt = project.getModifiedAt();
    }
}
