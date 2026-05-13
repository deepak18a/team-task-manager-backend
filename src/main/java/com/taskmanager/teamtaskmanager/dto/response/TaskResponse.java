package com.taskmanager.teamtaskmanager.dto.response;

import com.taskmanager.teamtaskmanager.enums.Priority;
import com.taskmanager.teamtaskmanager.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;

    private Long projectId;
    private String projectName;

    private Long assignedToId;
    private String assignedToName;
    private String assignedToEmail;

    private Long createdById;
    private String createdByName;
    private String createdByEmail;
}
