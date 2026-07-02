package com.seniormonitor.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManagerRequest {
    private String status;
    private String city;
    private String gu;
    private String dong;
}
