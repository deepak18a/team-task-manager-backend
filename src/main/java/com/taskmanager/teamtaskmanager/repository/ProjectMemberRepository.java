package com.taskmanager.teamtaskmanager.repository;

import com.taskmanager.teamtaskmanager.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByProjectIdAndUserIdAndIsDeletedFalse(Long projectId, Long userId);
    Optional<ProjectMember> findByProjectIdAndUserIdAndIsDeletedFalse(Long projectId, Long userId);

    List<ProjectMember> findByProjectIdAndIsDeletedFalse(Long projectId);

}
