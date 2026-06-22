package com.seniormonitor.server.controller;

import com.seniormonitor.server.dto.RegisterRequest;
import com.seniormonitor.server.dto.SignalRequest;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.entity.SignalLog;
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

    public MonitorController(ElderService elderService, SignalService signalService) {
        this.elderService = elderService;
        this.signalService = signalService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterRequest req) {
        return elderService.register(req);
    }

    @PostMapping("/signal")
    public Map<String, Object> receiveSignal(@RequestBody SignalRequest req) {
        return signalService.receive(req);
    }

    @GetMapping("/signals")
    public List<SignalLog> getSignals() {
        return signalService.getRecent();
    }

    @GetMapping("/users")
    public List<Senior> getUsers() {
        return elderService.getAll();
    }
}
