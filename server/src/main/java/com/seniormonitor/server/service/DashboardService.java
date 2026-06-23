package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.DashboardStatsResponse;
import com.seniormonitor.server.repository.ContactHistoryRepository;
import com.seniormonitor.server.repository.SeniorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final SeniorRepository seniorRepository;
    private final ContactHistoryRepository contactHistoryRepository;

    public DashboardService(SeniorRepository seniorRepository,
                            ContactHistoryRepository contactHistoryRepository) {
        this.seniorRepository = seniorRepository;
        this.contactHistoryRepository = contactHistoryRepository;
    }

    public DashboardStatsResponse getStats() {
        long totalSeniors = seniorRepository.countByIsDeleted("N");

        LocalDate today = LocalDate.now();
        LocalDateTime windowStart = LocalDateTime.of(today, LocalTime.of(5, 0));
        LocalDateTime windowEnd = LocalDateTime.of(today, LocalTime.of(10, 0));
        long alertCount = seniorRepository.findDangerSeniors(windowStart, windowEnd).size();

        LocalDateTime startOfDay = LocalDateTime.of(today, LocalTime.MIN);
        long confirmedTodayCount = contactHistoryRepository.countConfirmedToday(startOfDay);

        return new DashboardStatsResponse(totalSeniors, alertCount, confirmedTodayCount);
    }
}
