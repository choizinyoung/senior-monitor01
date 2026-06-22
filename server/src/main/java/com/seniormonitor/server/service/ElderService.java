package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.RegisterRequest;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.exception.BadRequestException;
import com.seniormonitor.server.exception.ConflictException;
import com.seniormonitor.server.exception.NotFoundException;
import com.seniormonitor.server.repository.SeniorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ElderService {

    private final SeniorRepository seniorRepository;

    public ElderService(SeniorRepository seniorRepository) {
        this.seniorRepository = seniorRepository;
    }

    public void register(RegisterRequest req) {
        if (req.getDeviceId() == null || req.getName() == null) {
            throw new BadRequestException("ERR_MISSING_FIELD", "deviceId, name은 필수 항목입니다.");
        }
        if (seniorRepository.findByDeviceId(req.getDeviceId()).isPresent()) {
            throw new ConflictException("ERR_DUPLICATE", "이미 등록된 기기입니다.");
        }

        Senior senior = new Senior();
        senior.setDeviceId(req.getDeviceId());
        senior.setName(req.getName());
        senior.setAge(req.getAge());
        senior.setPhone(req.getPhone());
        senior.setAddress(req.getAddress());
        seniorRepository.save(senior);
    }

    @Transactional(readOnly = true)
    public List<Senior> getAll() {
        return seniorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Senior getById(Long id) {
        return seniorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ERR_NOT_FOUND", "대상자를 찾을 수 없습니다."));
    }
}
