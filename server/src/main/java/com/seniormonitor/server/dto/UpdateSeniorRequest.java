package com.seniormonitor.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSeniorRequest {
    private String name;
    private Integer age;
    private String phone;
    private String city;
    private String gu;
    private String dong;
}
