package com.ioob.backend.service;

import com.ioob.backend.dto.ProjectRequestDto;
import com.ioob.backend.dto.ProjectResponseDto;

import java.util.List;

public interface ProjectService {
    List<ProjectResponseDto> getAllProjects();
    ProjectResponseDto getProjectById(String email, Long id);
    ProjectResponseDto createProject(ProjectRequestDto projectRequestDto);
    ProjectResponseDto updateProject(Long id, ProjectRequestDto projectRequestDto);
    void deleteProject(Long id);
    List<ProjectResponseDto> getUserProjects(String email);
}
