package com.taskmanager.teamtaskmanager.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskmanager.teamtaskmanager.dto.response.UserResponse;
import com.taskmanager.teamtaskmanager.entity.User;
import com.taskmanager.teamtaskmanager.exception.BadRequestException;
import com.taskmanager.teamtaskmanager.exception.ResourceNotFoundException;
import com.taskmanager.teamtaskmanager.mapper.UserMapper;
import com.taskmanager.teamtaskmanager.repository.UserRepository;
import com.taskmanager.teamtaskmanager.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findByIsDeletedFalseAndIsVerifiedTrue()
                .stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = getActiveUser(id);

        return UserMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getVerifiedUsers() {

        return userRepository.findByIsDeletedFalseAndIsVerifiedTrue()
                .stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {

        User user = getAnyUser(id);

        if (user.isDeleted()) {
            throw new BadRequestException(
                    "User is already deleted");
        }

        user.setDeleted(true);

        userRepository.save(user);
    }

    @Override
    public void restoreUser(Long id) {

        User user = getAnyUser(id);

        if (!user.isDeleted()) {
            throw new BadRequestException(
                    "User is already active");
        }

        user.setDeleted(false);

        userRepository.save(user);
    }

    private User getActiveUser(Long id) {

        return userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id));
    }

    private User getAnyUser(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id));
    }
}