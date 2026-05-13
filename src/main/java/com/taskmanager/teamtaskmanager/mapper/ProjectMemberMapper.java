package com.taskmanager.teamtaskmanager.mapper;

import com.taskmanager.teamtaskmanager.dto.request.ProjectMemberRequest;
import com.taskmanager.teamtaskmanager.dto.response.ProjectMemberResponse;
import com.taskmanager.teamtaskmanager.entity.ProjectMember;
import com.taskmanager.teamtaskmanager.enums.ProjectRole;

public class ProjectMemberMapper {


    public static ProjectMember toEntity(ProjectMemberRequest request) {
        if (request == null) return null;

        ProjectMember member = new ProjectMember();
        member.setRole(request.getRole() != null ? request.getRole() : ProjectRole.MEMBER);
        return member;
    }

    public static ProjectMemberResponse toResponse(ProjectMember member) {
        if (member == null) return null;

        ProjectMemberResponse response = new ProjectMemberResponse();
        response.setId(member.getId());
        response.setRole(member.getRole());
        response.setJoinedAt(member.getJoinedAt());

        if (member.getUser() != null) {
            response.setUserId(member.getUser().getId());
            response.setName(member.getUser().getName());
            response.setEmail(member.getUser().getEmail());
        }
        return response;
    }

}
