package com.taskmanager.teamtaskmanager.dto.response;

import lombok.Data;

@Data
public class DashboardResponse {

    private long totalTasks;
    private long todoTasks;
    private long inProgressTasks;
    private long reviewTasks;
    private long completedTasks;
    private long overdueTasks;
}
