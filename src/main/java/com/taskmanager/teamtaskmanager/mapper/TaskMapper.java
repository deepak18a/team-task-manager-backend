package com.taskmanager.teamtaskmanager.mapper;

import com.taskmanager.teamtaskmanager.dto.request.TaskRequest;
import com.taskmanager.teamtaskmanager.dto.response.TaskResponse;
import com.taskmanager.teamtaskmanager.entity.Task;
import com.taskmanager.teamtaskmanager.enums.Priority;
import com.taskmanager.teamtaskmanager.enums.TaskStatus;

public class TaskMapper {

    public static Task toEntity(TaskRequest request) {
        if (request == null) return null;

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO);

        return task;
    }

    public static TaskResponse toResponse(Task task) {
        if (task == null) return null;

        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());

        if (task.getProject() != null) {
            response.setProjectId(task.getProject().getId());
            response.setProjectName(task.getProject().getName());
        }

        if (task.getAssignedTo() != null) {
            response.setAssignedToId(task.getAssignedTo().getId());
            response.setAssignedToName(task.getAssignedTo().getName());
            response.setAssignedToEmail(task.getAssignedTo().getEmail());
        }

        if (task.getCreatedBy() != null) {
            response.setCreatedById(task.getCreatedBy().getId());
            response.setCreatedByName(task.getCreatedBy().getName());
            response.setCreatedByEmail(task.getCreatedBy().getEmail());
        }
        return response;
    }
}
