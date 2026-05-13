package com.taskmanager.teamtaskmanager.dto.request;

import com.taskmanager.teamtaskmanager.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectMemberRequest {

    @NotNull(message = "User id is required")
    private Long userId;

    private ProjectRole role;
}
