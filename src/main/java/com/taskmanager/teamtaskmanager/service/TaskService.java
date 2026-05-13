package com.taskmanager.teamtaskmanager.service;

import com.taskmanager.teamtaskmanager.dto.request.TaskRequest;
import com.taskmanager.teamtaskmanager.dto.request.TaskStatusUpdateRequest;
import com.taskmanager.teamtaskmanager.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(TaskRequest request, Long createdById);
    List<TaskResponse> getAllTasks();
    TaskResponse getTaskById(Long id);

    List<TaskResponse> getTasksByProject(Long projectId);
    List<TaskResponse> getTasksByUser(Long userId);
    TaskResponse updateTask(Long id, TaskRequest request);
    TaskResponse updateTaskStatus(Long id, TaskStatusUpdateRequest request);

    void deleteTask(Long id);
    void restoreTask(Long id);
}
