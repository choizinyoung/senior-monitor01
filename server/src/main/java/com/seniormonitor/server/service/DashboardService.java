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

        String gu   = RegionAccess.guFilter(manager);
        String dong = RegionAccess.dongFilter(manager);

        long totalSeniors        = countActive(gu, dong);
        long alertCount          = countByStatus("확인요망", gu, dong);
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        long confirmedTodayCount = countContact("확인완료", startOfDay, gu, dong);
        long emergencyTodayCount = countContact("응급호출", startOfDay, gu, dong);

        return new DashboardStatsResponse(totalSeniors, alertCount, confirmedTodayCount, emergencyTodayCount);
    }

    private long countActive(String gu, String dong) {
        if (gu != null && dong != null) return seniorRepository.countActiveByGuAndDong(gu, dong);
        if (gu != null)                 return seniorRepository.countActiveByGu(gu);
        return seniorRepository.countAllActive();
    }

    private long countByStatus(String status, String gu, String dong) {
        if (gu != null && dong != null) return seniorRepository.countByStatusAndGuAndDong(status, gu, dong);
        if (gu != null)                 return seniorRepository.countByStatusAndGu(status, gu);
        return seniorRepository.countByStatus(status);
    }

    private long countContact(String resultStatus, LocalDateTime startOfDay, String gu, String dong) {
        if (gu != null && dong != null) return contactHistoryRepository.countByResultStatusAndRegionToday(resultStatus, startOfDay, gu, dong);
        if (gu != null)                 return contactHistoryRepository.countByResultStatusAndGuToday(resultStatus, startOfDay, gu);
        return contactHistoryRepository.countByResultStatusToday(resultStatus, startOfDay);
    }
}
