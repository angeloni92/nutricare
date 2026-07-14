package com.angeloni.nutricare.service;

import com.angeloni.nutricare.dto.CopilotDeviceCodeDto;

import java.util.concurrent.CompletableFuture;

public interface CopilotDeviceFlowService {
    CopilotDeviceCodeDto startDeviceFlow();
    CompletableFuture<Void> pollForToken(String deviceCode, int intervalSeconds);
}