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
        if (RegionAccess.isUnassigned(manager)) {
            return List.of();
        }

        // MANAGER는 자신의 관할지역을 벗어난 gu/dong 조회 파라미터를 무시하고 본인 지역으로 강제 제한한다.
        String effectiveGu = manager.isMaster() ? emptyToNull(gu) : manager.gu();
        String effectiveDong = manager.isMaster()
                ? emptyToNull(dong)
                : (manager.dong() != null ? manager.dong() : emptyToNull(dong));

        List<Senior> seniors = seniorRepository.findByStatusAndRegion("확인요망", effectiveGu, effectiveDong);
        return seniors.stream().map(AlertResponse::new).toList();
    }

    private static String emptyToNull(String value) {
        return (value == null || value.isEmpty()) ? null : value;
    }
}
