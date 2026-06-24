package com.seniormonitor.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConfirmRequest {
    private String managerName;
    private String resultStatus;
    private String memo;
    private LocalDateTime contactedAt;
}
