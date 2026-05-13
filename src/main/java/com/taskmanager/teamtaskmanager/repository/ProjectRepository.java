package com.taskmanager.teamtaskmanager.repository;

import com.taskmanager.teamtaskmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByIdAndIsDeletedFalse(Long id);
    List<Project> findByIsDeletedFalse();


}
