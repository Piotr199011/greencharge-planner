package com.example.GreenCharge.Planner.service;

import com.example.GreenCharge.Planner.client.CarbonIntensityClient;
import com.example.GreenCharge.Planner.model.carbon.CarbonFuelMix;
import com.example.GreenCharge.Planner.model.carbon.CarbonGenerationData;
import com.example.GreenCharge.Planner.model.carbon.CarbonGenerationResponse;
import com.example.GreenCharge.Planner.model.carbon.GenerationMixItem;
import com.example.GreenCharge.Planner.model.dto.DailyAverageMixResponse;
import com.example.GreenCharge.Planner.model.dto.DailyFuelMix;
import com.example.GreenCharge.Planner.util.EnergyCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EnergyMixServiceTest {

    @Mock
    private CarbonIntensityClient carbonClient;

    @InjectMocks
    private EnergyMixService service;

    // Test 1: client returns null (Mono.empty instead of Mono.just(null))
    @Test
    void shouldReturnEmptyListWhenClientReturnsNull() {
        when(carbonClient.getGenerationDataMulti(any(), any()))
                .thenReturn(Mono.empty());

        List<DailyAverageMixResponse> result = service.getDailyAverageMixForThreeDays().block();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Test 2: client returns empty list
    @Test
    void shouldReturnEmptyListWhenClientReturnsEmptyList() {
        when(carbonClient.getGenerationDataMulti(any(), any()))
                .thenReturn(Mono.just(Collections.emptyList()));

        List<DailyAverageMixResponse> result =
                service.getDailyAverageMixForThreeDays().block();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    // Test 3: Single Interval Average
    @Test
    void shouldCalculateDailyAverageForSingleInterval() {
        CarbonGenerationResponse response = createResponseWithIntervals(Collections.singletonList(createDefaultMix()));

        when(carbonClient.getGenerationDataMulti(any(), any()))
                .thenReturn(Mono.just(Collections.singletonList(response)));

        List<DailyAverageMixResponse> result = service.getDailyAverageMixForThreeDays().block();

        assertEquals(1, result.size());
        DailyAverageMixResponse daily = result.get(0);

        assertEquals(20.0, daily.getAverageMix().getBiomass());
        assertEquals(30.0, daily.getAverageMix().getNuclear());
        assertEquals(10.0, daily.getAverageMix().getHydro());
        assertEquals(15.0, daily.getAverageMix().getWind());
        assertEquals(25.0, daily.getAverageMix().getSolar());
        assertEquals(0.0, daily.getAverageMix().getGas());
        assertEquals(0.0, daily.getAverageMix().getCoal());
        assertEquals(0.0, daily.getAverageMix().getOther());
    }

    // Test 4: Average for multiple intervals on the same day
    @Test
    void shouldCalculateAverageValuesForMultipleIntervalsInSameDay() {
        List<GenerationMixItem> interval1 = createDefaultMix();
        List<GenerationMixItem> interval2 = createDefaultMix();
        interval2.get(0).setPercentage(40.0);
        interval2.get(1).setPercentage(20.0);

        CarbonGenerationResponse response = createResponseWithIntervals(Arrays.asList(interval1, interval2));

        when(carbonClient.getGenerationDataMulti(any(), any()))
                .thenReturn(Mono.just(Collections.singletonList(response)));

        List<DailyAverageMixResponse> result = service.getDailyAverageMixForThreeDays().block();

        DailyAverageMixResponse daily = result.get(0);

        assertEquals(30.0, daily.getAverageMix().getBiomass());
        assertEquals(25.0, daily.getAverageMix().getNuclear());
    }

    // Test 5: grouping by date and sorting
    @Test
    void shouldGroupIntervalsByDateAndSortResultsAscending() {
        CarbonGenerationResponse response1 = createResponseWithIntervals(Collections.singletonList(createDefaultMix()));
        CarbonGenerationResponse response2 = createResponseWithIntervals(Collections.singletonList(createDefaultMix()));
        response2.getData().get(0).setFrom(ZonedDateTime.now().plusDays(1));
        response2.getData().get(0).setTo(ZonedDateTime.now().plusDays(1).plusMinutes(30));

        when(carbonClient.getGenerationDataMulti(any(), any()))
                .thenReturn(Mono.just(Arrays.asList(response2, response1)));

        List<DailyAverageMixResponse> result = service.getDailyAverageMixForThreeDays().block();

        assertTrue(result.get(0).getDate().isBefore(result.get(1).getDate()));
    }

    // Test 6: null handling in generationMix
    @Test
    void shouldHandleNullGenerationMixGracefully() {
        CarbonGenerationData data = new CarbonGenerationData();
        data.setFrom(ZonedDateTime.now());
        data.setTo(ZonedDateTime.now().plusMinutes(30));
        data.setGenerationMix(null);

        CarbonGenerationResponse response = new CarbonGenerationResponse();
        response.setData(Collections.singletonList(data));

        when(carbonClient.getGenerationDataMulti(any(), any()))
                .thenReturn(Mono.just(Collections.singletonList(response)));

        List<DailyAverageMixResponse> result = service.getDailyAverageMixForThreeDays().block();

        assertNotNull(result);
        assertEquals(0.0, result.get(0).getAverageMix().getBiomass());
    }

    // Test 7: Assigning unknown fuels to the "other" category
    @Test
    void shouldAssignUnknownFuelTypeToOtherCategory() {
        List<GenerationMixItem> mixItems = createDefaultMix();
        GenerationMixItem unknown = new GenerationMixItem();
        unknown.setFuel("foo");
        unknown.setPercentage(50.0);
        mixItems.add(unknown);

        CarbonGenerationResponse response = createResponseWithIntervals(Collections.singletonList(mixItems));

        when(carbonClient.getGenerationDataMulti(any(), any()))
                .thenReturn(Mono.just(Collections.singletonList(response)));

        List<DailyAverageMixResponse> result = service.getDailyAverageMixForThreeDays().block();

        assertEquals(50.0, result.get(0).getAverageMix().getOther());
    }

    @Test
    void shouldCalculateCleanEnergyPercentageCorrectly() {
        List<GenerationMixItem> mixItems = createDefaultMix();
        CarbonGenerationResponse response = createResponseWithIntervals(Collections.singletonList(mixItems));

        when(carbonClient.getGenerationDataMulti(any(), any()))
                .thenReturn(Mono.just(Collections.singletonList(response)));

        List<DailyAverageMixResponse> result = service.getDailyAverageMixForThreeDays().block();

        DailyAverageMixResponse daily = result.get(0);
        DailyFuelMix avgMix = daily.getAverageMix();
        double expectedClean = EnergyCalculator.calculateCleanPercentage(
                new CarbonFuelMix() {{
                    setBiomass(avgMix.getBiomass());
                    setHydro(avgMix.getHydro());
                    setWind(avgMix.getWind());
                    setSolar(avgMix.getSolar());
                    setNuclear(avgMix.getNuclear());
                    setGas(avgMix.getGas());
                    setCoal(avgMix.getCoal());
                    setOther(avgMix.getOther());
                }}
        );

        assertEquals(expectedClean, daily.getCleanPercentage());
    }

    // Test 9: Propagating an exception from the client
    @Test
    void shouldPropagateExceptionWhenClientFails() {
        when(carbonClient.getGenerationDataMulti(any(), any()))
                .thenReturn(Mono.error(new RuntimeException("API failure")));

        assertThrows(RuntimeException.class, () -> service.getDailyAverageMixForThreeDays());
    }

    // Auxiliary methods
    private List<GenerationMixItem> createDefaultMix() {
        List<GenerationMixItem> mixItems = new ArrayList<>();

        GenerationMixItem biomass = new GenerationMixItem();
        biomass.setFuel("biomass");
        biomass.setPercentage(20.0);
        mixItems.add(biomass);

        GenerationMixItem nuclear = new GenerationMixItem();
        nuclear.setFuel("nuclear");
        nuclear.setPercentage(30.0);
        mixItems.add(nuclear);

        GenerationMixItem hydro = new GenerationMixItem();
        hydro.setFuel("hydro");
        hydro.setPercentage(10.0);
        mixItems.add(hydro);

        GenerationMixItem wind = new GenerationMixItem();
        wind.setFuel("wind");
        wind.setPercentage(15.0);
        mixItems.add(wind);

        GenerationMixItem solar = new GenerationMixItem();
        solar.setFuel("solar");
        solar.setPercentage(25.0);
        mixItems.add(solar);

        GenerationMixItem gas = new GenerationMixItem();
        gas.setFuel("gas");
        gas.setPercentage(0.0);
        mixItems.add(gas);

        GenerationMixItem coal = new GenerationMixItem();
        coal.setFuel("coal");
        coal.setPercentage(0.0);
        mixItems.add(coal);

        GenerationMixItem other = new GenerationMixItem();
        other.setFuel("other");
        other.setPercentage(0.0);
        mixItems.add(other);

        return mixItems;
    }

    private CarbonGenerationResponse createResponseWithIntervals(List<List<GenerationMixItem>> intervals) {
        List<CarbonGenerationData> dataList = new ArrayList<>();
        for (List<GenerationMixItem> mix : intervals) {
            CarbonGenerationData data = new CarbonGenerationData();
            data.setFrom(ZonedDateTime.now());
            data.setTo(ZonedDateTime.now().plusMinutes(30));
            data.setGenerationMix(mix != null ? mix : Collections.emptyList());
            dataList.add(data);
        }
        CarbonGenerationResponse response = new CarbonGenerationResponse();
        response.setData(dataList);
        return response;
    }
}
