package com.taskmanager.teamtaskmanager.service;

import com.taskmanager.teamtaskmanager.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);

    List<UserResponse> getVerifiedUsers();

    void deleteUser(Long id);
    void restoreUser(Long id);
}
