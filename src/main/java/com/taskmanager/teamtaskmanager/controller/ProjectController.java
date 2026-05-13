package com.taskmanager.teamtaskmanager.controller;

import com.taskmanager.teamtaskmanager.dto.request.ProjectRequest;
import com.taskmanager.teamtaskmanager.dto.response.ProjectResponse;
import com.taskmanager.teamtaskmanager.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/{userId}")
    public ResponseEntity<ProjectResponse> createProject(@PathVariable Long userId, @Valid @RequestBody ProjectRequest request) {

        return new ResponseEntity<>(projectService.createProject(request, userId),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {

        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok("Project deleted successfully");
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<String> restoreProject(@PathVariable Long id) {
        projectService.restoreProject(id);
        return ResponseEntity.ok("Project restored successfully");
    }
}
