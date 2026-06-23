package com.seniormonitor.server.dto;

import com.seniormonitor.server.entity.Senior;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class AlertResponse {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private final Long id;
    private final String name;
    private final Integer age;
    private final String phone;
    private final String city;
    private final String gu;
    private final String dong;
    private final String status;
    private final String severity;
    private final String registeredAt;

    public AlertResponse(Senior senior) {
        this.id = senior.getId();
        this.name = senior.getName();
        this.age = senior.getAge();
        this.phone = senior.getPhone();
        this.city = senior.getCity();
        this.gu = senior.getGu();
        this.dong = senior.getDong();
        this.status = senior.getStatus();
        this.severity = "high";
        this.registeredAt = senior.getRegisteredAt().format(FMT);
    }
}
