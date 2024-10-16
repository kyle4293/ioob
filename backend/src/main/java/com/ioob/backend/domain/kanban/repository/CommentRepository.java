package com.ioob.backend.domain.kanban.repository;

import com.ioob.backend.domain.kanban.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskId(Long taskId); // 테스크 ID로 댓글 조회
}
