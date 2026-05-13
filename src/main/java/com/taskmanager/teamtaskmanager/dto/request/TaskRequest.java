package com.taskmanager.teamtaskmanager.dto.request;

import com.taskmanager.teamtaskmanager.enums.Priority;
import com.taskmanager.teamtaskmanager.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {

    @NotBlank(message = "Task title is required")
    private String title;
    private String description;

    @NotNull(message = "Project id is required")
    private Long projectId;

    @NotNull(message = "Assigned user id is required")
    private Long assignedToUserId;

    private LocalDate dueDate;


    private Priority priority;
    private TaskStatus status;

}
