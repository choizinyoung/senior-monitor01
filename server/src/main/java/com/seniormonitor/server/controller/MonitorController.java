package com.seniormonitor.server.controller;

import com.seniormonitor.server.dto.AlertResponse;
import com.seniormonitor.server.dto.ApiResponse;
import com.seniormonitor.server.dto.ConfirmRequest;
import com.seniormonitor.server.dto.ContactHistoryResponse;
import com.seniormonitor.server.dto.DashboardStatsResponse;
import com.seniormonitor.server.dto.RegisterRequest;
import com.seniormonitor.server.dto.UpdateSeniorRequest;
import com.seniormonitor.server.dto.SignalRequest;
import com.seniormonitor.server.entity.ContactHistory;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.entity.SignalLog;
import com.seniormonitor.server.security.CurrentManager;
import com.seniormonitor.server.service.AlertService;
import com.seniormonitor.server.service.ContactHistoryService;
import com.seniormonitor.server.service.DashboardService;
import com.seniormonitor.server.service.ElderService;
import com.seniormonitor.server.service.SignalService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class MonitorController {

    private final ElderService elderService;
    private final SignalService signalService;
    private final AlertService alertService;
    private final ContactHistoryService contactHistoryService;
    private final DashboardService dashboardService;

    public MonitorController(ElderService elderService,
                              SignalService signalService,
                              AlertService alertService,
                              ContactHistoryService contactHistoryService,
                              DashboardService dashboardService) {
        this.elderService = elderService;
        this.signalService = signalService;
        this.alertService = alertService;
        this.contactHistoryService = contactHistoryService;
        this.dashboardService = dashboardService;
    }

    // 대시보드 통계
    @GetMapping("/api/dashboard/stats")
    public ApiResponse<DashboardStatsResponse> getDashboardStats(@AuthenticationPrincipal CurrentManager manager) {
        return ApiResponse.ok(dashboardService.getStats(manager));
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
            @RequestParam(required = false) String gu,
            @RequestParam(required = false) String dong,
            @AuthenticationPrincipal CurrentManager manager) {
        return ApiResponse.ok(alertService.getDangerAlerts(severity, gu, dong, manager));
    }

    // API 3: 대상자 전체 목록
    @GetMapping("/api/seniors")
    public ApiResponse<List<Senior>> getSeniors(@AuthenticationPrincipal CurrentManager manager) {
        return ApiResponse.ok(elderService.getAll(manager));
    }

    // API 3-1: 개별 대상자 상세
    @GetMapping("/api/seniors/{seniorId}")
    public ApiResponse<Senior> getSenior(@PathVariable Long seniorId,
                                          @AuthenticationPrincipal CurrentManager manager) {
        return ApiResponse.ok(elderService.getById(seniorId, manager));
    }

    // API 3-4: 대상자 정보 수정 (APK)
    @PostMapping("/api/seniors/{seniorId}/update")
    public ApiResponse<Senior> updateSenior(@PathVariable Long seniorId,
                                             @RequestBody UpdateSeniorRequest req,
                                             @AuthenticationPrincipal CurrentManager manager) {
        return ApiResponse.ok(elderService.update(seniorId, req, manager));
    }

    // API 3-5: 대상자 소프트 삭제
    @DeleteMapping("/api/seniors/{seniorId}")
    public ApiResponse<Void> deleteSenior(@PathVariable Long seniorId,
                                           @AuthenticationPrincipal CurrentManager manager) {
        elderService.softDelete(seniorId, manager);
        return ApiResponse.ok(null);
    }

    // API 3-2: 개별 대상자 연락 이력
    @GetMapping("/api/seniors/{seniorId}/contacts")
    public ApiResponse<List<ContactHistory>> getContactHistory(@PathVariable Long seniorId,
                                                                 @AuthenticationPrincipal CurrentManager manager) {
        return ApiResponse.ok(contactHistoryService.getHistory(seniorId, manager));
    }

    // API 4-1: 전체 처리내역 조회
    @GetMapping("/api/contacts")
    public ApiResponse<List<ContactHistoryResponse>> getAllContacts(
            @RequestParam(required = false) String resultStatus,
            @AuthenticationPrincipal CurrentManager manager) {
        return ApiResponse.ok(contactHistoryService.getAllHistory(resultStatus, manager));
    }

    // API 4: 확인 처리 (status 변경 + 연락 이력 기록)
    @PostMapping("/api/alerts/{seniorId}/confirm")
    public ApiResponse<Senior> confirm(@PathVariable Long seniorId,
                                       @RequestBody ConfirmRequest req,
                                       @AuthenticationPrincipal CurrentManager manager) {
        return ApiResponse.ok(contactHistoryService.confirm(seniorId, req, manager));
    }

    // API 3-3: 개별 대상자 신호 이력 (기본: 오늘, 날짜 범위 검색 가능)
    @GetMapping("/api/seniors/{seniorId}/signals")
    public ApiResponse<List<SignalLog>> getSignalHistory(
            @PathVariable Long seniorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal CurrentManager manager) {
        return ApiResponse.ok(signalService.getSignalsBySenior(seniorId, from, to, manager));
    }

    // API 5: 기상신호 수신 (APK)
    @PostMapping("/api/signal")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> receiveSignal(@RequestBody SignalRequest req) {
        signalService.receive(req);
        return ApiResponse.ok(null);
    }

    // API 6: 전체 신호 목록 (기본: 오늘, 날짜 범위 검색 가능)
    @GetMapping("/api/signals")
    public ApiResponse<List<SignalLog>> getSignals(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal CurrentManager manager) {
        return ApiResponse.ok(signalService.getRecent(from, to, manager));
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
