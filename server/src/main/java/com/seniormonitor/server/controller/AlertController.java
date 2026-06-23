package com.seniormonitor.server.controller;

import com.seniormonitor.server.dto.AlertResponse;
import com.seniormonitor.server.service.AlertService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/alerts")
    public List<AlertResponse> getAlerts(
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String gu,
            @RequestParam(required = false) String dong
    ) {
        return alertService.getDangerAlerts(severity, gu, dong);
    }
}
