package com.ioob.backend.domain.kanban.repository;

import com.ioob.backend.domain.kanban.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByBoardId(Long boardId);

    List<Task> findByUserEmail(String email);

    List<Task> findByUserId(Long userId);

    void deleteByUserId(Long id);
}
