package com.ioob.backend.domain.kanban.repository;

import com.ioob.backend.domain.kanban.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
