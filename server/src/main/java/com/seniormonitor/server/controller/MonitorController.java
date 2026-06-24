package com.seniormonitor.server.controller;

import com.seniormonitor.server.dto.AlertResponse;
import com.seniormonitor.server.dto.ApiResponse;
import com.seniormonitor.server.dto.ConfirmRequest;
import com.seniormonitor.server.dto.RegisterRequest;
import com.seniormonitor.server.dto.UpdateSeniorRequest;
import com.seniormonitor.server.dto.SignalRequest;
import com.seniormonitor.server.entity.ContactHistory;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.entity.SignalLog;
import com.seniormonitor.server.service.AlertService;
import com.seniormonitor.server.service.ContactHistoryService;
import com.seniormonitor.server.service.ElderService;
import com.seniormonitor.server.service.SignalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class MonitorController {

    private final ElderService elderService;
    private final SignalService signalService;
    private final AlertService alertService;
    private final ContactHistoryService contactHistoryService;

    public MonitorController(ElderService elderService,
                              SignalService signalService,
                              AlertService alertService,
                              ContactHistoryService contactHistoryService) {
        this.elderService = elderService;
        this.signalService = signalService;
        this.alertService = alertService;
        this.contactHistoryService = contactHistoryService;
    }

    // API 1: 대상자 등록 (APK)
    @PostMapping("/api/seniors/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Senior> register(@RequestBody RegisterRequest req) {
        return ApiResponse.ok(elderService.register(req));
    }

    // API 2: 위험 대상자 알림 목록 (신호 미수신자)
    @GetMapping("/api/alerts")
    public ApiResponse<List<AlertResponse>> getAlerts(
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String district) {
        return ApiResponse.ok(alertService.getDangerAlerts(severity, district));
    }

    // API 3: 대상자 전체 목록
    @GetMapping("/api/seniors")
    public ApiResponse<List<Senior>> getSeniors() {
        return ApiResponse.ok(elderService.getAll());
    }

    // API 3-1: 개별 대상자 상세
    @GetMapping("/api/seniors/{seniorId}")
    public ApiResponse<Senior> getSenior(@PathVariable Long seniorId) {
        return ApiResponse.ok(elderService.getById(seniorId));
    }

    // API 3-4: 대상자 정보 수정
    @PostMapping("/api/seniors/{seniorId}/update")
    public ApiResponse<Senior> updateSenior(@PathVariable Long seniorId,
                                             @RequestBody UpdateSeniorRequest req) {
        return ApiResponse.ok(elderService.update(seniorId, req));
    }

    // API 3-2: 개별 대상자 연락 이력
    @GetMapping("/api/seniors/{seniorId}/contacts")
    public ApiResponse<List<ContactHistory>> getContactHistory(@PathVariable Long seniorId) {
        return ApiResponse.ok(contactHistoryService.getHistory(seniorId));
    }

    // API 4: 확인 처리 (status 변경 + 연락 이력 기록)
    @PostMapping("/api/alerts/{seniorId}/confirm")
    public ApiResponse<Senior> confirm(@PathVariable Long seniorId,
                                       @RequestBody ConfirmRequest req) {
        return ApiResponse.ok(contactHistoryService.confirm(seniorId, req));
    }

    // API 3-3: 개별 대상자 신호 이력
    @GetMapping("/api/seniors/{seniorId}/signals")
    public ApiResponse<List<SignalLog>> getSignalHistory(@PathVariable Long seniorId) {
        return ApiResponse.ok(signalService.getSignalsBySenior(seniorId));
    }

    // API 5: 기상신호 수신 (APK)
    @PostMapping("/api/signal")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> receiveSignal(@RequestBody SignalRequest req) {
        signalService.receive(req);
        return ApiResponse.ok(null);
    }

    // API 6: 전체 신호 목록 (최근 200건)
    @GetMapping("/api/signals")
    public ApiResponse<List<SignalLog>> getSignals() {
        return ApiResponse.ok(signalService.getRecent());
    }

    // 하위 호환 — APK가 기존 경로를 그대로 쓸 경우
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Senior> registerLegacy(@RequestBody RegisterRequest req) {
        return ApiResponse.ok(elderService.register(req));
    }

    @PostMapping("/signal")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> signalLegacy(@RequestBody SignalRequest req) {
        signalService.receive(req);
        return ApiResponse.ok(null);
    }
}
