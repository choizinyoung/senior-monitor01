package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.SignalRequest;
import com.seniormonitor.server.entity.Elder;
import com.seniormonitor.server.entity.SignalLog;
import com.seniormonitor.server.repository.ElderRepository;
import com.seniormonitor.server.repository.SignalLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SignalService {

    private final SignalLogRepository signalLogRepository;
    private final ElderRepository elderRepository;

    public SignalService(SignalLogRepository signalLogRepository, ElderRepository elderRepository) {
        this.signalLogRepository = signalLogRepository;
        this.elderRepository = elderRepository;
    }

    public Map<String, Object> receive(SignalRequest req) {
        SignalLog log = new SignalLog();
        log.setDeviceId(req.getDeviceId());
        log.setDeviceModel(req.getDeviceModel());
        signalLogRepository.save(log);

        String name = elderRepository.findByDeviceId(req.getDeviceId())
                .map(Elder::getName)
                .orElse("알 수 없는 분");

        System.out.println("[신호] " + name + " | " + log.getSignalTime());
        return Map.of("success", true);
    }

    @Transactional(readOnly = true)
    public List<SignalLog> getRecent() {
        return signalLogRepository.findTop200ByOrderBySignalTimeDesc();
    }
}
