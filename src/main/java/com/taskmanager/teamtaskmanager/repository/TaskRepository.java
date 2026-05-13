package com.taskmanager.teamtaskmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.teamtaskmanager.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndIsDeletedFalse(Long id);

    List<Task> findByIsDeletedFalse();

    List<Task> findByProjectIdAndIsDeletedFalse(Long projectId);

    List<Task> findByAssignedToIdAndIsDeletedFalse(Long userId);

}