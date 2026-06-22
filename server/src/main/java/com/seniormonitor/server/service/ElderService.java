package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.RegisterRequest;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.repository.SeniorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ElderService {

    private final SeniorRepository seniorRepository;

    public ElderService(SeniorRepository seniorRepository) {
        this.seniorRepository = seniorRepository;
    }

    public Map<String, Object> register(RegisterRequest req) {
        if (req.getDeviceId() == null || req.getName() == null) {
            return Map.of("error", "필수 정보 누락");
        }

        if (seniorRepository.findByDeviceId(req.getDeviceId()).isPresent()) {
            return Map.of("error", "이미 등록된 기기입니다");
        }

        Senior senior = new Senior();
        senior.setDeviceId(req.getDeviceId());
        senior.setName(req.getName());
        senior.setAge(req.getAge());
        senior.setPhone(req.getPhone());
        senior.setAddress(req.getAddress());
        seniorRepository.save(senior);

        return Map.of("success", true);
    }

    @Transactional(readOnly = true)
    public List<Senior> getAll() {
        return seniorRepository.findAll();
    }
}
