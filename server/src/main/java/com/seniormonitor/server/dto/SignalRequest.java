package com.seniormonitor.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignalRequest {
    private String deviceId;
    private String status;
}
