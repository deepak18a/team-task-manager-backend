package com.taskmanager.teamtaskmanager.service;

import com.taskmanager.teamtaskmanager.dto.response.DashboardResponse;

public interface DashboardService {

    DashboardResponse getDashboard();
    DashboardResponse getProjectDashboard(Long projectId);
}
