package com.seniormonitor.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String deviceId;
    private String name;
    private Integer age;
    private String phone;
    private String address;
}
