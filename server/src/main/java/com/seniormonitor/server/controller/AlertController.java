package com.seniormonitor.server.controller;

import com.seniormonitor.server.dto.AlertResponse;
import com.seniormonitor.server.security.CurrentManager;
import com.seniormonitor.server.service.AlertService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/alerts")
    public List<AlertResponse> getAlerts(
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String gu,
            @RequestParam(required = false) String dong,
            @AuthenticationPrincipal CurrentManager manager
    ) {
        return alertService.getDangerAlerts(severity, gu, dong, manager);
    }
}
