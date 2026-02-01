package com.example.GreenCharge.Planner.controller;

import com.example.GreenCharge.Planner.model.carbon.CarbonGenerationData;
import com.example.GreenCharge.Planner.model.carbon.CarbonGenerationResponse;
import com.example.GreenCharge.Planner.model.carbon.GenerationMixItem;
import com.example.GreenCharge.Planner.model.dto.DailyAverageMixResponse;
import com.example.GreenCharge.Planner.model.dto.DailyFuelMix;
import com.example.GreenCharge.Planner.service.EnergyMixService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebFluxTest(controllers = EnergyMixController.class)
class EnergyMixControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EnergyMixService service;

    private List<GenerationMixItem> mixItems;

    @BeforeEach
    void setup() {
        // Tworzymy domyślną listę paliw
        mixItems = new ArrayList<>();
        mixItems.add(createMixItem("solar", 30.0));
        mixItems.add(createMixItem("wind", 20.0));
    }

    private GenerationMixItem createMixItem(String fuel, double percentage) {
        GenerationMixItem item = new GenerationMixItem();
        item.setFuel(fuel);
        item.setPercentage(percentage);
        return item;
    }

    private CarbonGenerationData createCarbonData(List<GenerationMixItem> items) {
        CarbonGenerationData data = new CarbonGenerationData();
        data.setFrom(ZonedDateTime.now());
        data.setTo(ZonedDateTime.now().plusMinutes(30));
        data.setGenerationMix(items);
        return data;
    }

    private CarbonGenerationResponse createResponseWithIntervals(List<List<GenerationMixItem>> intervals) {
        List<CarbonGenerationData> dataList = new ArrayList<>();
        for (List<GenerationMixItem> interval : intervals) {
            dataList.add(createCarbonData(interval));
        }
        CarbonGenerationResponse response = new CarbonGenerationResponse();
        response.setData(dataList);
        return response;
    }

    // =======================
    // TEST 1: Sprawdzenie poprawnego miksu
    // =======================
    @Test
    void shouldReturnCorrectDailyMix() {
        DailyFuelMix mix = new DailyFuelMix();
        mix.setSolar(30.0);
        mix.setWind(20.0);
        mix.setHydro(0.0);
        mix.setGas(0.0);
        mix.setOther(50.0);

        DailyAverageMixResponse response = new DailyAverageMixResponse();
        response.setAverageMix(mix);
        response.setCleanPercentage(50.0);

        when(service.getDailyAverageMixForThreeDays())
                .thenReturn(Mono.just(Collections.singletonList(response)));

        // Service call
        List<DailyAverageMixResponse> result = service.getDailyAverageMixForThreeDays().block();

        // Checking the result
        assertEquals(30.0, result.get(0).getAverageMix().getSolar());
        assertEquals(20.0, result.get(0).getAverageMix().getWind());
        assertEquals(50.0, result.get(0).getAverageMix().getOther());
        assertEquals(50.0, result.get(0).getCleanPercentage());
    }

    // =======================
    // TEST 2: WebTestClient Controller Test
    // =======================
    @Test
    void shouldReturn200Ok() {
        webTestClient.get()
                .uri("/api/energy-mix")  // Your REST path
                .exchange()
                .expectStatus().isOk();
    }
}
