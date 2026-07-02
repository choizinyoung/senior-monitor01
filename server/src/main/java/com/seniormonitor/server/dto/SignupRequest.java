package com.seniormonitor.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String name;
    private String username;
    private String password;
    private String phone;
    private String email;
}
