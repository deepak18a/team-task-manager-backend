package com.taskmanager.teamtaskmanager.dto.response;

import com.taskmanager.teamtaskmanager.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}
