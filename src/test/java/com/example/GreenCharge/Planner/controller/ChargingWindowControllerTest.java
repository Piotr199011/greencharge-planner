package com.example.GreenCharge.Planner.controller;

import com.example.GreenCharge.Planner.model.dto.OptimalChargingWindowResponse;
import com.example.GreenCharge.Planner.service.ChargingWindowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebFluxTest(controllers = ChargingWindowController.class)
class ChargingWindowControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ChargingWindowService chargingWindowService;

    private OptimalChargingWindowResponse sampleResponse;

    private final ZonedDateTime fixedStart = ZonedDateTime.parse("2026-01-22T10:00:00Z");
    private final ZonedDateTime fixedEnd = ZonedDateTime.parse("2026-01-22T12:00:00Z");

    @BeforeEach
    void setUp() {
        sampleResponse = new OptimalChargingWindowResponse();
        sampleResponse.setStart(fixedStart);
        sampleResponse.setEnd(fixedEnd);
        sampleResponse.setAverageCleanPercentage(75.0);
    }

    @Test
    void testGetOptimalChargingWindow_ValidHours_ReturnsOk() {
        // We return Mono.just(sampleResponse)
        Mockito.when(chargingWindowService.findOptimalChargingWindow(2))
                .thenReturn(Mono.just(sampleResponse));

        webTestClient.get()
                .uri("/api/optimal-charging?hours=2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.averageCleanPercentage").isEqualTo(75.0)
                .jsonPath("$.start").isEqualTo(fixedStart.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .jsonPath("$.end").isEqualTo(fixedEnd.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    @Test
    void testGetOptimalChargingWindow_InvalidHours_ReturnsBadRequest() {
        webTestClient.get()
                .uri("/api/optimal-charging?hours=10") // >6
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetOptimalChargingWindow_NoContent() {
        // We return Mono.empty() instead of null
        Mockito.when(chargingWindowService.findOptimalChargingWindow(3))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/optimal-charging?hours=3")
                .exchange()
                .expectStatus().isNoContent();
    }
}
