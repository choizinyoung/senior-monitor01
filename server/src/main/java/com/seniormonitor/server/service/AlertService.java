package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.AlertResponse;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.repository.SeniorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AlertService {

    private final SeniorRepository seniorRepository;

    public AlertService(SeniorRepository seniorRepository) {
        this.seniorRepository = seniorRepository;
    }

    public List<AlertResponse> getDangerAlerts(String severity, String district) {
        LocalDate today = LocalDate.now();
        LocalDateTime windowStart = LocalDateTime.of(today, LocalTime.of(5, 0));
        LocalDateTime windowEnd = LocalDateTime.of(today, LocalTime.of(10, 0));

        List<Senior> seniors;
        if (district != null && !district.isEmpty()) {
            seniors = seniorRepository.findDangerSeniorsByDistrict(windowStart, windowEnd, district);
        } else {
            seniors = seniorRepository.findDangerSeniors(windowStart, windowEnd);
        }

        List<AlertResponse> alerts = seniors.stream()
                .map(AlertResponse::new)
                .toList();

        if (severity != null && !severity.isEmpty()) {
            return alerts.stream()
                    .filter(a -> severity.equals(a.getSeverity()))
                    .toList();
        }

        return alerts;
    }
}
