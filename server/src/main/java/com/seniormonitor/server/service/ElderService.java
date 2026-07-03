package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.RegisterRequest;
import com.seniormonitor.server.dto.UpdateSeniorRequest;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.exception.BadRequestException;
import com.seniormonitor.server.exception.ConflictException;
import com.seniormonitor.server.exception.NotFoundException;
import com.seniormonitor.server.repository.SeniorRepository;
import com.seniormonitor.server.security.CurrentManager;
import com.seniormonitor.server.security.RegionAccess;
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

    public Senior register(RegisterRequest req) {
        if (req.getDeviceId() == null || req.getName() == null) {
            throw new BadRequestException("ERR_MISSING_FIELD", "deviceId, name은 필수 항목입니다.");
        }
        if (seniorRepository.findByDeviceId(req.getDeviceId()).isPresent()) {
            throw new ConflictException("ERR_DUPLICATE", "이미 등록된 기기입니다.");
        }
        if (req.getPhone() != null && seniorRepository.existsByPhone(req.getPhone())) {
            throw new ConflictException("ERR_DUPLICATE_PHONE", "이미 등록된 번호입니다.");
        }

        Senior senior = new Senior();
        senior.setDeviceId(req.getDeviceId());
        senior.setName(req.getName());
        senior.setStatus("정상");
        senior.setAge(req.getAge());
        senior.setPhone(req.getPhone());
        senior.setCity(req.getCity());
        senior.setGu(req.getGu());
        senior.setDong(req.getDong());
        return seniorRepository.save(senior);
    }

    @Transactional(readOnly = true)
    public List<Senior> getAll(CurrentManager manager) {
        if (RegionAccess.isUnassigned(manager)) {
            return List.of();
        }
        String city = RegionAccess.cityFilter(manager);
        String gu   = RegionAccess.guFilter(manager);
        String dong = RegionAccess.dongFilter(manager);
        if (gu != null && dong != null) return seniorRepository.findActiveByGuAndDong(gu, dong);
        if (gu != null)                 return seniorRepository.findActiveByGu(gu);
        if (city != null)               return seniorRepository.findActiveByCity(city);
        return seniorRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public Senior getById(Long id, CurrentManager manager) {
        Senior senior = seniorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ERR_NOT_FOUND", "대상자를 찾을 수 없습니다."));
        RegionAccess.assertAccessible(senior, manager);
        return senior;
    }

    public Senior update(Long id, UpdateSeniorRequest req, CurrentManager manager) {
        Senior senior = seniorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ERR_NOT_FOUND", "대상자를 찾을 수 없습니다."));
        RegionAccess.assertAccessible(senior, manager);

        if (req.getName()  != null) senior.setName(req.getName());
        if (req.getAge()   != null) senior.setAge(req.getAge());
        if (req.getPhone() != null) senior.setPhone(req.getPhone());
        if (req.getCity()  != null) senior.setCity(req.getCity());
        if (req.getGu()    != null) senior.setGu(req.getGu());
        if (req.getDong()  != null) senior.setDong(req.getDong());

        return seniorRepository.save(senior);
    }

    public void softDelete(Long id, CurrentManager manager) {
        Senior senior = seniorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ERR_NOT_FOUND", "대상자를 찾을 수 없습니다."));
        RegionAccess.assertAccessible(senior, manager);

        if ("확인요망".equals(senior.getStatus())) {
            throw new BadRequestException("ERR_CANNOT_DELETE", "확인요망 상태의 대상자는 삭제할 수 없습니다.");
        }

        senior.setIsDeleted("Y");
        seniorRepository.save(senior);
    }
}
