package com.seniormonitor.server.controller;

import com.seniormonitor.server.dto.ApiResponse;
import com.seniormonitor.server.dto.CrashLogRequest;
import com.seniormonitor.server.service.CrashLogService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrashLogController {

    private final CrashLogService crashLogService;

    public CrashLogController(CrashLogService crashLogService) {
        this.crashLogService = crashLogService;
    }

    // API: 크래시 로그 제출 (APK)
    @PostMapping("/api/crash-logs")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> reportCrash(@RequestBody CrashLogRequest req) {
        crashLogService.report(req);
        return ApiResponse.ok(null);
    }
}
