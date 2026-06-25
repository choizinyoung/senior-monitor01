package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.AlertResponse;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.repository.SeniorRepository;
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

    public List<AlertResponse> getDangerAlerts(String severity, String gu, String dong) {
        boolean hasGu   = gu != null && !gu.isEmpty();
        boolean hasDong = dong != null && !dong.isEmpty();

        List<Senior> seniors;
        if (hasGu && hasDong) {
            seniors = seniorRepository.findByStatusAndIsDeletedAndGuAndDong("확인요망", "N", gu, dong);
        } else if (hasGu) {
            seniors = seniorRepository.findByStatusAndIsDeletedAndGu("확인요망", "N", gu);
        } else {
            seniors = seniorRepository.findByStatusAndIsDeleted("확인요망", "N");
        }

        return seniors.stream().map(AlertResponse::new).toList();
    }
}
