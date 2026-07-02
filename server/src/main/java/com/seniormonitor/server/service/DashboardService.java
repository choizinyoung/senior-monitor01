package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.DashboardStatsResponse;
import com.seniormonitor.server.repository.ContactHistoryRepository;
import com.seniormonitor.server.repository.SeniorRepository;
import com.seniormonitor.server.security.CurrentManager;
import com.seniormonitor.server.security.RegionAccess;
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

    public DashboardStatsResponse getStats(CurrentManager manager) {
        if (RegionAccess.isUnassigned(manager)) {
            return new DashboardStatsResponse(0, 0, 0, 0);
        }

        String gu = RegionAccess.guFilter(manager);
        String dong = RegionAccess.dongFilter(manager);

        long totalSeniors = seniorRepository.countActiveByRegion(gu, dong);
        long alertCount   = seniorRepository.countByStatusAndRegion("확인요망", gu, dong);

        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        long confirmedTodayCount = contactHistoryRepository.countConfirmedToday(startOfDay, gu, dong);
        long emergencyTodayCount = contactHistoryRepository.countEmergencyToday(startOfDay, gu, dong);

        return new DashboardStatsResponse(totalSeniors, alertCount, confirmedTodayCount, emergencyTodayCount);
    }
}
