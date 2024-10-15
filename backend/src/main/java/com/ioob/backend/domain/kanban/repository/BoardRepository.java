package com.ioob.backend.domain.kanban.repository;

import com.ioob.backend.domain.kanban.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByProjectId(Long projectId);
}
