package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.RegisterRequest;
import com.seniormonitor.server.entity.Elder;
import com.seniormonitor.server.repository.ElderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ElderService {

    private final ElderRepository elderRepository;

    public ElderService(ElderRepository elderRepository) {
        this.elderRepository = elderRepository;
    }

    public Map<String, Object> register(RegisterRequest req) {
        if (req.getDeviceId() == null || req.getName() == null) {
            return Map.of("error", "필수 정보 누락");
        }

        Elder elder = elderRepository.findByDeviceId(req.getDeviceId())
                .orElse(new Elder());
        elder.setDeviceId(req.getDeviceId());
        elder.setName(req.getName());
        elder.setBirthdate(req.getBirthdate());
        elderRepository.save(elder);

        System.out.println("사용자 등록: " + req.getName());
        return Map.of("success", true);
    }

    @Transactional(readOnly = true)
    public List<Elder> getAll() {
        return elderRepository.findAll();
    }
}
