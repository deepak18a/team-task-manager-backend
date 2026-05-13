package com.taskmanager.teamtaskmanager.service.impl;

import com.taskmanager.teamtaskmanager.dto.request.ProjectMemberRequest;
import com.taskmanager.teamtaskmanager.dto.response.ProjectMemberResponse;
import com.taskmanager.teamtaskmanager.entity.Project;
import com.taskmanager.teamtaskmanager.entity.ProjectMember;
import com.taskmanager.teamtaskmanager.entity.User;
import com.taskmanager.teamtaskmanager.exception.AccessDeniedException;
import com.taskmanager.teamtaskmanager.exception.BadRequestException;
import com.taskmanager.teamtaskmanager.exception.DuplicateResourceException;
import com.taskmanager.teamtaskmanager.exception.ResourceNotFoundException;
import com.taskmanager.teamtaskmanager.mapper.ProjectMemberMapper;
import com.taskmanager.teamtaskmanager.repository.ProjectMemberRepository;
import com.taskmanager.teamtaskmanager.repository.ProjectRepository;
import com.taskmanager.teamtaskmanager.repository.UserRepository;
import com.taskmanager.teamtaskmanager.service.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public ProjectMemberResponse addMember(Long projectId, ProjectMemberRequest request) {

        Project project = projectRepository.findByIdAndIsDeletedFalse(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));


        User loggedInUser = getLoggedInUser();
        checkProjectOwner(project, loggedInUser);


        User user = userRepository.findByIdAndIsDeletedFalse(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (projectMemberRepository.existsByProjectIdAndUserIdAndIsDeletedFalse(projectId, request.getUserId())) {
            throw new DuplicateResourceException("User already added to this project");
        }

        ProjectMember member = ProjectMemberMapper.toEntity(request);
        member.setProject(project);
        member.setUser(user);

        ProjectMember savedMember = projectMemberRepository.save(member);
        return ProjectMemberMapper.toResponse(savedMember);
    }

    @Override
    public List<ProjectMemberResponse> getProjectMembers(Long projectId) {
        projectRepository.findByIdAndIsDeletedFalse(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        return projectMemberRepository.findByProjectIdAndIsDeletedFalse(projectId)
                .stream()
                .map(ProjectMemberMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void removeMember(Long projectId, Long userId) {

        Project project = projectRepository.findByIdAndIsDeletedFalse(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id: " + projectId));

        User loggedInUser = getLoggedInUser();
        checkProjectOwner(project, loggedInUser);

        ProjectMember member = projectMemberRepository.findByProjectIdAndUserIdAndIsDeletedFalse(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project member not found"));

        if (member.isDeleted()) {
            throw new BadRequestException("Project member is already removed");
        }
        member.setDeleted(true);
        projectMemberRepository.save(member);
    }

    private User getLoggedInUser() {
        String loggedInEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmailAndIsDeletedFalse(loggedInEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
    }

    private void checkProjectOwner(Project project, User loggedInUser) {
        if (!project.getCreatedBy().getId().equals(loggedInUser.getId())) {
            throw new AccessDeniedException("Only project owner can manage members");
        }
    }
}
