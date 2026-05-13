package com.taskmanager.teamtaskmanager.service;

import com.taskmanager.teamtaskmanager.dto.request.ProjectRequest;
import com.taskmanager.teamtaskmanager.dto.response.ProjectResponse;

import java.util.List;

public interface ProjectService {


    ProjectResponse createProject(ProjectRequest request, Long userId);
    List<ProjectResponse> getAllProjects();
    ProjectResponse getProjectById(Long id);

    ProjectResponse updateProject(Long id, ProjectRequest request);
    void deleteProject(Long id);
    void restoreProject(Long id);
}
