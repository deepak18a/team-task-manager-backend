package com.taskmanager.teamtaskmanager.controller;

import com.taskmanager.teamtaskmanager.dto.response.DashboardResponse;
import com.taskmanager.teamtaskmanager.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<DashboardResponse> getProjectDashboard(@PathVariable Long projectId) {
        return ResponseEntity.ok(dashboardService.getProjectDashboard(projectId));
    }
}
