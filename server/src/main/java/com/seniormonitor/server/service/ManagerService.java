package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.ManagerResponse;
import com.seniormonitor.server.dto.UpdateManagerRequest;
import com.seniormonitor.server.entity.Manager;
import com.seniormonitor.server.exception.BadRequestException;
import com.seniormonitor.server.exception.NotFoundException;
import com.seniormonitor.server.repository.ManagerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ManagerService {

    private static final Set<String> ALLOWED_STATUS = Set.of("PENDING", "APPROVED");

    private final ManagerRepository managerRepository;

    public ManagerService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Transactional(readOnly = true)
    public List<ManagerResponse> getAll(String status) {
        List<Manager> managers = status != null
                ? managerRepository.findAllByStatusOrderByCreatedAtDesc(status)
                : managerRepository.findAllByOrderByCreatedAtDesc();
        return managers.stream().map(ManagerResponse::new).toList();
    }

    public ManagerResponse update(Long id, UpdateManagerRequest req) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ERR_NOT_FOUND", "담당자를 찾을 수 없습니다."));

        if (req.getStatus() != null) {
            if (!ALLOWED_STATUS.contains(req.getStatus())) {
                throw new BadRequestException("ERR_INVALID_VALUE", "status는 PENDING / APPROVED 중 하나여야 합니다.");
            }
            manager.setStatus(req.getStatus());
        }
        if (req.getCity() != null) manager.setCity(req.getCity());
        if (req.getGu()   != null) manager.setGu(req.getGu());
        if (req.getDong() != null) manager.setDong(req.getDong());

        return new ManagerResponse(managerRepository.save(manager));
    }
}
