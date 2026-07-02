package com.seniormonitor.server.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private final String token;
    private final ManagerResponse manager;

    public LoginResponse(String token, ManagerResponse manager) {
        this.token = token;
        this.manager = manager;
    }
}
