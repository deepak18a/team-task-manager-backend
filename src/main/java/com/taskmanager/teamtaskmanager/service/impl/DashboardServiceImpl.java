package com.taskmanager.teamtaskmanager.service.impl;

import com.taskmanager.teamtaskmanager.dto.response.DashboardResponse;
import com.taskmanager.teamtaskmanager.entity.Project;
import com.taskmanager.teamtaskmanager.entity.Task;
import com.taskmanager.teamtaskmanager.entity.User;
import com.taskmanager.teamtaskmanager.enums.Role;
import com.taskmanager.teamtaskmanager.enums.TaskStatus;
import com.taskmanager.teamtaskmanager.exception.AccessDeniedException;
import com.taskmanager.teamtaskmanager.exception.ResourceNotFoundException;
import com.taskmanager.teamtaskmanager.repository.ProjectMemberRepository;
import com.taskmanager.teamtaskmanager.repository.ProjectRepository;
import com.taskmanager.teamtaskmanager.repository.TaskRepository;
import com.taskmanager.teamtaskmanager.repository.UserRepository;
import com.taskmanager.teamtaskmanager.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Override
    public DashboardResponse getDashboard() {

        User loggedInUser = getLoggedInUser();
        List<Task> tasks;
        if (loggedInUser.getRole() == Role.ADMIN) {
            tasks = taskRepository.findByIsDeletedFalse();
        } else {
            tasks = taskRepository.findByAssignedToIdAndIsDeletedFalse(loggedInUser.getId());
        }
        return buildDashboard(tasks);
    }

    @Override
    public DashboardResponse getProjectDashboard(Long projectId) {

        Project project = projectRepository.findByIdAndIsDeletedFalse(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id: " + projectId
                ));

        User loggedInUser = getLoggedInUser();

        boolean isOwner = project.getCreatedBy().getId().equals(loggedInUser.getId());

        boolean isMember = projectMemberRepository
                .existsByProjectIdAndUserIdAndIsDeletedFalse(
                        projectId,
                        loggedInUser.getId()
                );

        boolean isAdmin = loggedInUser.getRole() == Role.ADMIN;

        if (!isOwner && !isMember && !isAdmin) {
            throw new AccessDeniedException(
                    "You are not authorized to view this project dashboard"
            );
        }

        List<Task> tasks = taskRepository.findByProjectIdAndIsDeletedFalse(projectId);

        return buildDashboard(tasks);
    }

    private DashboardResponse buildDashboard(List<Task> tasks) {

        DashboardResponse response = new DashboardResponse();

        long totalTasks = tasks.size();
        long todoTasks = 0;
        long inProgressTasks = 0;
        long reviewTasks = 0;
        long completedTasks = 0;
        long overdueTasks = 0;

        LocalDate today = LocalDate.now();

        for (Task task : tasks) {

            if (task.getStatus() == TaskStatus.TODO) {
                todoTasks++;
            } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                inProgressTasks++;
            } else if (task.getStatus() == TaskStatus.REVIEW) {
                reviewTasks++;
            } else if (task.getStatus() == TaskStatus.COMPLETED) {
                completedTasks++;
            }

            if (task.getDueDate() != null
                    && task.getDueDate().isBefore(today)
                    && task.getStatus() != TaskStatus.COMPLETED) {
                overdueTasks++;
            }
        }

        response.setTotalTasks(totalTasks);
        response.setTodoTasks(todoTasks);
        response.setInProgressTasks(inProgressTasks);
        response.setReviewTasks(reviewTasks);
        response.setCompletedTasks(completedTasks);
        response.setOverdueTasks(overdueTasks);

        return response;
    }

    private User getLoggedInUser() {
        String loggedInEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmailAndIsDeletedFalse(loggedInEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
    }
}