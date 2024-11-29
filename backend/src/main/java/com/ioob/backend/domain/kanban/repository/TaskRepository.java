package com.ioob.backend.domain.kanban.repository;

import com.ioob.backend.domain.kanban.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByBoardId(Long boardId);

    List<Task> findByAssignedToId(Long userId);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.files WHERE t.id = :taskId")
    Optional<Task> findByIdWithFiles(@Param("taskId") Long taskId);
}
