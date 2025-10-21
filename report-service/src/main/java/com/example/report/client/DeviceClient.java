package com.example.report.client;

import com.example.report.dto.DeviceDTO;
import com.example.report.response.DeviceResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class DeviceClient {
    private final WebClient webClient;
    public DeviceClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://device-service")
                .build();
    }

    public Mono<List<DeviceDTO>> getAllDevices(String authHeader) {
        return webClient.get()
                .uri("http://localhost:8081/device/report")
                .header("Authorization", authHeader)
                .header("X-Service-Source", "report-service")
                .retrieve()
                .bodyToMono(DeviceResponse.class)
                .map(DeviceResponse::getDeviceList);
    }

    public Mono<List<DeviceDTO>> getAllActiveDevices(String authHeader) {
        return webClient.get()
                .uri("http://localhost:8081/device/report/active")
                .header("Authorization", authHeader)
                .header("X-Service-Source", "report-service")
                .retrieve()
                .bodyToMono(DeviceResponse.class)
                .map(DeviceResponse::getDeviceList);
    }
}
