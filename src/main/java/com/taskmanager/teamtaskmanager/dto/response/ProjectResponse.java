package com.taskmanager.teamtaskmanager.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    private Long createdById;
    private String createdByName;
    private String createdByEmail;

    private List<ProjectMemberResponse> members;
    private List<TaskResponse> tasks;

}
