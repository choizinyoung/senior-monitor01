package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.AlertResponse;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.repository.SeniorRepository;
import com.seniormonitor.server.security.CurrentManager;
import com.seniormonitor.server.security.RegionAccess;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AlertService {

    private final SeniorRepository seniorRepository;

    public AlertService(SeniorRepository seniorRepository) {
        this.seniorRepository = seniorRepository;
    }

    public List<AlertResponse> getDangerAlerts(String severity, String gu, String dong, CurrentManager manager) {
        return getAlertsByStatus("확인요망", gu, dong, manager);
    }

    public List<AlertResponse> getMaintainedAlerts(String gu, String dong, CurrentManager manager) {
        return getAlertsByStatus("확인요망유지", gu, dong, manager);
    }

    private List<AlertResponse> getAlertsByStatus(String status, String gu, String dong, CurrentManager manager) {
        if (RegionAccess.isUnassigned(manager)) {
            return List.of();
        }

        String effectiveGu = manager.isMaster() ? emptyToNull(gu) : manager.gu();
        String effectiveDong = manager.isMaster()
                ? emptyToNull(dong)
                : (manager.dong() != null ? manager.dong() : emptyToNull(dong));

        List<Senior> seniors = querySeniorsByStatus(status, effectiveGu, effectiveDong);
        return seniors.stream().map(AlertResponse::new).toList();
    }

    private List<Senior> querySeniorsByStatus(String status, String gu, String dong) {
        if (gu != null && dong != null) {
            return seniorRepository.findByStatusAndGuAndDong(status, gu, dong);
        } else if (gu != null) {
            return seniorRepository.findByStatusAndGu(status, gu);
        } else {
            return seniorRepository.findByStatus(status);
        }
    }

    private static String emptyToNull(String value) {
        return (value == null || value.isEmpty()) ? null : value;
    }
}
