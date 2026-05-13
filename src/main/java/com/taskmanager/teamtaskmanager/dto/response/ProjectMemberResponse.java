package com.taskmanager.teamtaskmanager.dto.response;

import com.taskmanager.teamtaskmanager.enums.ProjectRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectMemberResponse {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private ProjectRole role;
    private LocalDateTime joinedAt;

}
