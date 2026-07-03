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

    public List<AlertResponse> getDangerAlerts(String severity, String name, String city, String gu, String dong, CurrentManager manager) {
        return getAlertsByStatus("확인요망", name, city, gu, dong, manager);
    }

    public List<AlertResponse> getMaintainedAlerts(String name, String city, String gu, String dong, CurrentManager manager) {
        return getAlertsByStatus("확인요망유지", name, city, gu, dong, manager);
    }

    private List<AlertResponse> getAlertsByStatus(String status, String name, String city, String gu, String dong, CurrentManager manager) {
        if (RegionAccess.isUnassigned(manager)) {
            return List.of();
        }

        String effectiveCity = manager.isMaster() ? emptyToNull(city) : manager.city();
        String effectiveGu   = manager.isMaster() ? emptyToNull(gu)
                : (manager.gu() != null ? manager.gu() : emptyToNull(gu));
        String effectiveDong = manager.isMaster() ? emptyToNull(dong)
                : (manager.dong() != null ? manager.dong() : emptyToNull(dong));

        return querySeniorsByStatus(status, effectiveCity, effectiveGu, effectiveDong)
                .stream()
                .filter(s -> name == null || name.isBlank() || s.getName().contains(name))
                .map(AlertResponse::new)
                .toList();
    }

    private List<Senior> querySeniorsByStatus(String status, String city, String gu, String dong) {
        if (gu != null && dong != null) return seniorRepository.findByStatusAndGuAndDong(status, gu, dong);
        if (gu != null)                 return seniorRepository.findByStatusAndGu(status, gu);
        if (city != null)               return seniorRepository.findByStatusAndCity(status, city);
        return seniorRepository.findByStatus(status);
    }

    private static String emptyToNull(String value) {
        return (value == null || value.isEmpty()) ? null : value;
    }
}
