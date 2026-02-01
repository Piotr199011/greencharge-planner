package com.example.GreenCharge.Planner.service;

import com.example.GreenCharge.Planner.client.CarbonIntensityClient;
import com.example.GreenCharge.Planner.model.carbon.*;
import com.example.GreenCharge.Planner.model.dto.OptimalChargingWindowResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChargingWindowServiceTest {

    private CarbonIntensityClient carbonClient;
    private ChargingWindowService service;

    @BeforeEach
    void setUp() {
        carbonClient = mock(CarbonIntensityClient.class);
        service = new ChargingWindowService(carbonClient);
    }

    @Test
    void shouldFindOptimalChargingWindow() {
        // Preparing test data
        ZonedDateTime now = ZonedDateTime.now().withMinute(0).withSecond(0).withNano(0);

        // We create 30-minute intervals
        GenerationMixItem solar = new GenerationMixItem();
        solar.setFuel("solar");
        solar.setPercentage(100.0);

        CarbonGenerationData data1 = new CarbonGenerationData();
        data1.setGenerationMix(Collections.singletonList(solar));
        data1.setFrom(now);
        data1.setTo(now.plusMinutes(30));

        CarbonGenerationData data2 = new CarbonGenerationData();
        data2.setGenerationMix(Collections.singletonList(solar));
        data2.setFrom(now.plusMinutes(30));
        data2.setTo(now.plusMinutes(60));

        CarbonGenerationResponse response = new CarbonGenerationResponse();
        response.setData(Arrays.asList(data1, data2));

        // Client mocking
        when(carbonClient.getGenerationDataMulti(any(), any()))
                .thenReturn(Mono.just(Collections.singletonList(response)));

        // Calling the method being tested
        OptimalChargingWindowResponse result = service
                .findOptimalChargingWindow(1).block(); //

        // Assertions
        assertNotNull(result);
        assertEquals(100.0, result.getAverageCleanPercentage());
        assertEquals(now, result.getStart());
        assertEquals(now.plusMinutes(60), result.getEnd());
    }

    @Test
    void shouldReturnNullIfNoData() {
        when(carbonClient.getGenerationDataMulti(any(), any()))
                .thenReturn(Mono.just(Collections.emptyList()));

        OptimalChargingWindowResponse result = service.findOptimalChargingWindow(1).block();

        assertNull(result);
    }

    @Test
    void shouldThrowExceptionForInvalidHours() {
        assertThrows(IllegalArgumentException.class, () -> service.findOptimalChargingWindow(0).block());
        assertThrows(IllegalArgumentException.class, () -> service.findOptimalChargingWindow(7).block());
    }
}
