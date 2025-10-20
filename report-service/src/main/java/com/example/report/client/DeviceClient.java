package com.example.report.client;

import com.example.report.dto.DeviceDTO;
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

    public Mono<List<DeviceDTO>> getAllDevices() {
        return webClient.get()
                .uri("http://localhost:8081/device/")
                .retrieve()
                .bodyToFlux(DeviceDTO.class)
                .collectList();
    }
}
