package com.taskmanager.teamtaskmanager.service;

import com.taskmanager.teamtaskmanager.dto.request.ProjectMemberRequest;
import com.taskmanager.teamtaskmanager.dto.response.ProjectMemberResponse;

import java.util.List;

public interface ProjectMemberService {

    ProjectMemberResponse addMember(Long projectId, ProjectMemberRequest request);
    List<ProjectMemberResponse> getProjectMembers(Long projectId);
    void removeMember(Long projectId, Long userId);
}
