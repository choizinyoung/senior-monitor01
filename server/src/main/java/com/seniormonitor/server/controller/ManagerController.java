package com.seniormonitor.server.controller;

import com.seniormonitor.server.dto.ApiResponse;
import com.seniormonitor.server.dto.ManagerResponse;
import com.seniormonitor.server.dto.UpdateManagerRequest;
import com.seniormonitor.server.service.ManagerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    // API: 담당자 목록 조회 (승인 전/후 포함, status로 필터 가능) — MASTER 전용
    @GetMapping("/api/managers")
    public ApiResponse<List<ManagerResponse>> getManagers(@RequestParam(required = false) String status) {
        return ApiResponse.ok(managerService.getAll(status));
    }

    // API: 담당자 정보 변경 (승인/승인해제, 관할지역 변경) — MASTER 전용
    @PostMapping("/api/managers/{managerId}/update")
    public ApiResponse<ManagerResponse> updateManager(@PathVariable Long managerId,
                                                        @RequestBody UpdateManagerRequest req) {
        return ApiResponse.ok(managerService.update(managerId, req));
    }
}
