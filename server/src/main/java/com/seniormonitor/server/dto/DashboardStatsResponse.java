package com.seniormonitor.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardStatsResponse {
    private final long totalSeniors;
    private final long alertCount;
    private final long confirmedTodayCount;
    private final long emergencyTodayCount;
}
