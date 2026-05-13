package com.taskmanager.teamtaskmanager.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.taskmanager.teamtaskmanager.dto.request.TaskRequest;
import com.taskmanager.teamtaskmanager.dto.request.TaskStatusUpdateRequest;
import com.taskmanager.teamtaskmanager.dto.response.TaskResponse;
import com.taskmanager.teamtaskmanager.entity.Project;
import com.taskmanager.teamtaskmanager.entity.Task;
import com.taskmanager.teamtaskmanager.entity.User;
import com.taskmanager.teamtaskmanager.exception.AccessDeniedException;
import com.taskmanager.teamtaskmanager.exception.BadRequestException;
import com.taskmanager.teamtaskmanager.exception.DuplicateResourceException;
import com.taskmanager.teamtaskmanager.exception.ResourceNotFoundException;
import com.taskmanager.teamtaskmanager.mapper.TaskMapper;
import com.taskmanager.teamtaskmanager.repository.ProjectRepository;
import com.taskmanager.teamtaskmanager.repository.TaskRepository;
import com.taskmanager.teamtaskmanager.repository.UserRepository;
import com.taskmanager.teamtaskmanager.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public TaskResponse createTask(TaskRequest request, Long createdById) {

        String loggedInEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User loggedInUser = userRepository
                .findByEmailAndIsDeletedFalse(loggedInEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged in user not found"));

        if (!Objects.equals(loggedInUser.getId(), createdById)) {
            throw new AccessDeniedException(
                    "You cannot create task for another user");
        }

        Project project = projectRepository
                .findByIdAndIsDeletedFalse(request.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: "
                                        + request.getProjectId()));

        String newTaskTitle = normalizeText(request.getTitle());

        boolean taskExists = taskRepository
                .findByProjectIdAndIsDeletedFalse(request.getProjectId())
                .stream()
                .anyMatch(task ->
                        normalizeText(task.getTitle())
                                .equals(newTaskTitle));

        if (taskExists) {
            throw new DuplicateResourceException(
                    "Task already exists with title: "
                            + request.getTitle());
        }

        User assignedUser = userRepository
                .findByIdAndIsDeletedFalse(request.getAssignedToUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Assigned user not found with id: "
                                        + request.getAssignedToUserId()));

        Task task = TaskMapper.toEntity(request);

        task.setProject(project);
        task.setAssignedTo(assignedUser);
        task.setCreatedBy(loggedInUser);

        return TaskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public List<TaskResponse> getAllTasks() {

        return taskRepository.findByIsDeletedFalse()
                .stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse getTaskById(Long id) {

        return TaskMapper.toResponse(getActiveTask(id));
    }

    @Override
    public List<TaskResponse> getTasksByProject(Long projectId) {

        projectRepository.findByIdAndIsDeletedFalse(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: "
                                        + projectId));

        return taskRepository.findByProjectIdAndIsDeletedFalse(projectId)
                .stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTasksByUser(Long userId) {

        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: "
                                        + userId));

        return taskRepository.findByAssignedToIdAndIsDeletedFalse(userId)
                .stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse updateTask(Long id, TaskRequest request) {

        Task task = getActiveTask(id);

        Project project = projectRepository
                .findByIdAndIsDeletedFalse(request.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: "
                                        + request.getProjectId()));

        User assignedUser = userRepository
                .findByIdAndIsDeletedFalse(request.getAssignedToUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Assigned user not found with id: "
                                        + request.getAssignedToUserId()));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setProject(project);
        task.setAssignedTo(assignedUser);

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        return TaskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public TaskResponse updateTaskStatus(
            Long id,
            TaskStatusUpdateRequest request) {

        Task task = getActiveTask(id);

        task.setStatus(request.getStatus());

        return TaskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long id) {

        Task task = getAnyTask(id);

        if (task.isDeleted()) {
            throw new BadRequestException(
                    "Task is already deleted");
        }

        task.setDeleted(true);

        taskRepository.save(task);
    }

    @Override
    public void restoreTask(Long id) {

        Task task = getAnyTask(id);

        if (!task.isDeleted()) {
            throw new BadRequestException(
                    "Task is already active");
        }

        task.setDeleted(false);

        taskRepository.save(task);
    }

    private Task getActiveTask(Long id) {

        return taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id));
    }

    private Task getAnyTask(Long id) {

        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id));
    }

    private String normalizeText(String text) {

        return text.replaceAll("\\s+", "")
                .toLowerCase();
    }
}