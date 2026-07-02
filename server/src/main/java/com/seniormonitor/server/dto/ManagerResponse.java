package com.seniormonitor.server.dto;

import com.seniormonitor.server.entity.Manager;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ManagerResponse {

    private final Long id;
    private final String name;
    private final String username;
    private final String phone;
    private final String email;
    private final String city;
    private final String gu;
    private final String dong;
    private final String role;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ManagerResponse(Manager manager) {
        this.id = manager.getId();
        this.name = manager.getName();
        this.username = manager.getUsername();
        this.phone = manager.getPhone();
        this.email = manager.getEmail();
        this.city = manager.getCity();
        this.gu = manager.getGu();
        this.dong = manager.getDong();
        this.role = manager.getRole();
        this.status = manager.getStatus();
        this.createdAt = manager.getCreatedAt();
        this.updatedAt = manager.getUpdatedAt();
    }
}
