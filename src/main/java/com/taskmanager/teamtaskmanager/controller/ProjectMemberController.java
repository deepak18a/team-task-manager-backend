package com.taskmanager.teamtaskmanager.controller;

import com.taskmanager.teamtaskmanager.dto.request.ProjectMemberRequest;
import com.taskmanager.teamtaskmanager.dto.response.ProjectMemberResponse;
import com.taskmanager.teamtaskmanager.service.ProjectMemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {

    @Autowired
    private ProjectMemberService service;


    @PostMapping
    public ResponseEntity<ProjectMemberResponse> addMember(@PathVariable Long projectId,
            @Valid @RequestBody ProjectMemberRequest request) {

        return new ResponseEntity<>(service.addMember(projectId, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProjectMemberResponse>> getMembers(@PathVariable Long projectId) {
        return ResponseEntity.ok(service.getProjectMembers(projectId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long userId) {

        service.removeMember(projectId, userId);
        return ResponseEntity.ok("Project member removed successfully");
    }

}
