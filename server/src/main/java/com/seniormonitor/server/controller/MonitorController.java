package com.seniormonitor.server.controller;

import com.seniormonitor.server.dto.AlertResponse;
import com.seniormonitor.server.dto.RegisterRequest;
import com.seniormonitor.server.dto.SignalRequest;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.entity.SignalLog;
import com.seniormonitor.server.service.AlertService;
import com.seniormonitor.server.service.ElderService;
import com.seniormonitor.server.service.SignalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class MonitorController {

    private final ElderService elderService;
    private final SignalService signalService;
    private final AlertService alertService;

    public MonitorController(ElderService elderService,
                              SignalService signalService,
                              AlertService alertService) {
        this.elderService = elderService;
        this.signalService = signalService;
        this.alertService = alertService;
    }

    // API 1: APK 대상자 등록
    @PostMapping("/api/seniors/register")
    public Map<String, Object> register(@RequestBody RegisterRequest req) {
        return elderService.register(req);
    }

    // API 3: 대상자 목록 조회
    @GetMapping("/api/seniors")
    public List<Senior> getSeniors() {
        return elderService.getAll();
    }

    // API 5: 기상신호 수신 (APK → 서버)
    @PostMapping("/api/signal")
    public Map<String, Object> receiveSignal(@RequestBody SignalRequest req) {
        return signalService.receive(req);
    }

    // API 6: 최근 신호 목록 조회
    @GetMapping("/api/signals")
    public List<SignalLog> getSignals() {
        return signalService.getRecent();
    }

    // API 2: 위험 대상자 알림 목록 (신호 미수신자)
    @GetMapping("/api/alerts")
    public List<AlertResponse> getAlerts(
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String district) {
        return alertService.getDangerAlerts(severity, district);
    }

    // 하위 호환 — APK가 기존 경로를 그대로 쓸 경우
    @PostMapping("/register")
    public Map<String, Object> registerLegacy(@RequestBody RegisterRequest req) {
        return elderService.register(req);
    }

    @PostMapping("/signal")
    public Map<String, Object> signalLegacy(@RequestBody SignalRequest req) {
        return signalService.receive(req);
    }
}
