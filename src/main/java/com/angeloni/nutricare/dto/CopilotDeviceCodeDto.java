package com.angeloni.nutricare.dto;

import lombok.Data;

@Data
public class CopilotDeviceCodeDto {
    private String deviceCode;
    private String userCode;
    private String verificationUri;
    private int expiresIn;
    private int interval;
}