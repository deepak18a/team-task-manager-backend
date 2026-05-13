package com.taskmanager.teamtaskmanager.mapper;

import com.taskmanager.teamtaskmanager.dto.request.ProjectRequest;
import com.taskmanager.teamtaskmanager.dto.response.ProjectResponse;
import com.taskmanager.teamtaskmanager.entity.Project;

import java.util.stream.Collectors;

public class ProjectMapper {

    public static Project toEntity(ProjectRequest request) {
        if (request == null) return null;

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());

        return project;
    }

    public static ProjectResponse toResponse(Project project) {
        if (project == null) return null;

        ProjectResponse res = new ProjectResponse();
        res.setId(project.getId());
        res.setName(project.getName());
        res.setDescription(project.getDescription());
        res.setCreatedAt(project.getCreatedAt());

        if (project.getCreatedBy() != null) {
            res.setCreatedById(project.getCreatedBy().getId());
            res.setCreatedByName(project.getCreatedBy().getName());
            res.setCreatedByEmail(project.getCreatedBy().getEmail());
        }

        if (project.getMembers() != null) {
            res.setMembers(
                    project.getMembers()
                            .stream()
                            .filter(pm -> !pm.isDeleted())
                            .map(ProjectMemberMapper::toResponse)
                            .collect(Collectors.toList())
            );
        }

        if (project.getTasks() != null) {
            res.setTasks(
                    project.getTasks()
                            .stream()
                            .filter(t -> !t.isDeleted())
                            .map(TaskMapper::toResponse)
                            .collect(Collectors.toList())
            );
        }
        return res;
    }
}
