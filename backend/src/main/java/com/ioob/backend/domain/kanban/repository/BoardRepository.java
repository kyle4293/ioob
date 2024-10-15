package com.ioob.backend.repository;

import com.ioob.backend.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByProjectId(Long projectId);
}
