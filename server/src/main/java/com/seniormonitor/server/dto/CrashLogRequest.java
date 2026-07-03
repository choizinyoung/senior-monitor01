package com.seniormonitor.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CrashLogRequest {
    private String deviceId;
    private String message;
    private String appVersion;
    private LocalDateTime occurredAt;
}
