package com.ioob.backend.domain.kanban.repository;

import com.ioob.backend.domain.kanban.entity.UserProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProjectRoleRepository extends JpaRepository<UserProjectRole, Long> {

    // 특정 프로젝트에서의 사용자 역할 조회
    List<UserProjectRole> findByProjectId(Long projectId);

    List<UserProjectRole> findByUserEmailAndProjectId(String username, Long projectId);

    List<UserProjectRole> findByUserId(Long userId);
}
