package com.taskmanager.teamtaskmanager.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.taskmanager.teamtaskmanager.dto.request.ProjectRequest;
import com.taskmanager.teamtaskmanager.dto.response.ProjectResponse;
import com.taskmanager.teamtaskmanager.entity.Project;
import com.taskmanager.teamtaskmanager.entity.User;
import com.taskmanager.teamtaskmanager.exception.AccessDeniedException;
import com.taskmanager.teamtaskmanager.exception.BadRequestException;
import com.taskmanager.teamtaskmanager.exception.DuplicateResourceException;
import com.taskmanager.teamtaskmanager.exception.ResourceNotFoundException;
import com.taskmanager.teamtaskmanager.mapper.ProjectMapper;
import com.taskmanager.teamtaskmanager.repository.ProjectRepository;
import com.taskmanager.teamtaskmanager.repository.UserRepository;
import com.taskmanager.teamtaskmanager.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ProjectResponse createProject(ProjectRequest request, Long userId) {

        String loggedInEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User loggedInUser = userRepository
                .findByEmailAndIsDeletedFalse(loggedInEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Logged in user not found"));

        if (!Objects.equals(loggedInUser.getId(), userId)) {
            throw new AccessDeniedException(
                    "You cannot create project for another user");
        }

        String newProjectName = normalizeName(request.getName());

        boolean exists = projectRepository.findByIsDeletedFalse()
                .stream()
                .anyMatch(project ->
                        normalizeName(project.getName())
                                .equals(newProjectName));

        if (exists) {
            throw new DuplicateResourceException(
                    "Project already exists with name: "
                            + request.getName());
        }

        Project project = ProjectMapper.toEntity(request);

        project.setCreatedBy(loggedInUser);

        Project savedProject = projectRepository.save(project);

        return ProjectMapper.toResponse(savedProject);
    }

    @Override
    public List<ProjectResponse> getAllProjects() {

        return projectRepository.findByIsDeletedFalse()
                .stream()
                .map(ProjectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectResponse getProjectById(Long id) {

        Project project = getActiveProject(id);

        return ProjectMapper.toResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long id, ProjectRequest request) {

        Project project = getActiveProject(id);

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        Project updatedProject = projectRepository.save(project);

        return ProjectMapper.toResponse(updatedProject);
    }

    @Override
    public void deleteProject(Long id) {

        Project project = getAnyProject(id);

        if (project.isDeleted()) {
            throw new BadRequestException(
                    "Project is already deleted");
        }

        project.setDeleted(true);

        projectRepository.save(project);
    }

    @Override
    public void restoreProject(Long id) {

        Project project = getAnyProject(id);

        if (!project.isDeleted()) {
            throw new BadRequestException(
                    "Project is already active");
        }

        project.setDeleted(false);

        projectRepository.save(project);
    }

    private Project getActiveProject(Long id) {

        return projectRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id));
    }

    private Project getAnyProject(Long id) {

        return projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id));
    }

    private String normalizeName(String name) {

        return name.replaceAll("\\s+", "")
                .toLowerCase();
    }
}