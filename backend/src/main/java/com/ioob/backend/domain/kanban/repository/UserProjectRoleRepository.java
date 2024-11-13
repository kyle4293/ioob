package com.ioob.backend.domain.kanban.repository;

import com.ioob.backend.domain.auth.entity.User;
import com.ioob.backend.domain.kanban.entity.UserProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProjectRoleRepository extends JpaRepository<UserProjectRole, Long> {

    List<UserProjectRole> findByProjectId(Long projectId);

    List<UserProjectRole> findByUserId(Long userId);

    Optional<UserProjectRole> findByUserEmailAndProjectId(String username, Long projectId);

    Optional<UserProjectRole> findByUserAndProjectId(User user, Long projectId);

    boolean existsByUserEmailAndProjectId(String username, Long projectId);
}
