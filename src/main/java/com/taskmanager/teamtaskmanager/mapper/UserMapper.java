package com.taskmanager.teamtaskmanager.mapper;

import com.taskmanager.teamtaskmanager.dto.request.SignupRequest;
import com.taskmanager.teamtaskmanager.dto.response.UserResponse;
import com.taskmanager.teamtaskmanager.entity.User;
import com.taskmanager.teamtaskmanager.enums.Role;

public class UserMapper {

    public static User toEntity(SignupRequest req) {
        if (req == null) return null;

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setRole(req.getRole() != null ? req.getRole() : Role.MEMBER);

        return user;
    }

    public static UserResponse toResponse(User user) {
        if (user == null) return null;

        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setRole(user.getRole());
        res.setCreatedAt(user.getCreatedAt());

        return res;
    }
}
