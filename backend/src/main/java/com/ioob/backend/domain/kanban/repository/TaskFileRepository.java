package com.ioob.backend.domain.kanban.repository;

import com.ioob.backend.domain.kanban.entity.TaskFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskFileRepository extends JpaRepository<TaskFile, Long> {

    // 특정 Task에 연결된 파일 목록을 가져오는 메서드
    List<TaskFile> findByTaskId(Long taskId);
}
