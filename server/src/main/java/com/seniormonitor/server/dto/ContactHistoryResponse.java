package com.seniormonitor.server.dto;

import com.seniormonitor.server.entity.ContactHistory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ContactHistoryResponse {

    private final Long id;
    private final Long seniorId;
    private final String seniorName;
    private final String gu;
    private final String dong;
    private final String managerName;
    private final String resultStatus;
    private final String memo;
    private final LocalDateTime contactedAt;
    private final LocalDateTime createdAt;

    public ContactHistoryResponse(ContactHistory ch) {
        this.id = ch.getId();
        this.seniorId = ch.getSenior().getId();
        this.seniorName = ch.getSenior().getName();
        this.gu = ch.getSenior().getGu();
        this.dong = ch.getSenior().getDong();
        this.managerName = ch.getManagerName();
        this.resultStatus = ch.getResultStatus();
        this.memo = ch.getMemo();
        this.contactedAt = ch.getContactedAt();
        this.createdAt = ch.getCreatedAt();
    }
}
