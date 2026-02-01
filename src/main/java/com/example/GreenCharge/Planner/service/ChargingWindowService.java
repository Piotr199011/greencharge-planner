package com.example.GreenCharge.Planner.service;

import com.example.GreenCharge.Planner.client.CarbonIntensityClient;
import com.example.GreenCharge.Planner.model.carbon.*;
import com.example.GreenCharge.Planner.model.domain.CleanEnergyWindow;
import com.example.GreenCharge.Planner.model.domain.HalfHourInterval;
import com.example.GreenCharge.Planner.model.dto.OptimalChargingWindowResponse;
import com.example.GreenCharge.Planner.util.EnergyCalculator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ChargingWindowService {

    private final CarbonIntensityClient carbonClient;

    public ChargingWindowService(CarbonIntensityClient carbonClient) {
        this.carbonClient = carbonClient;
    }

    /*
     * Finds the optimal charging window (1-6h) in the next 48 hours from now.
     */
    public Mono<OptimalChargingWindowResponse> findOptimalChargingWindow(int hours) {
        if (hours < 1 || hours > 6) {
            return Mono.error(new IllegalArgumentException("Hours must be 1-6"));
        }

        // Start = now, finish = 48h from now
        ZonedDateTime from = ZonedDateTime.now().withSecond(0).withNano(0);
        ZonedDateTime to = from.plusHours(48);

        return carbonClient.getGenerationDataMulti(from, to)
                .timeout(Duration.ofSeconds(10))
                .onErrorReturn(Collections.emptyList())
                .flatMap(responses -> {
                    if (responses == null || responses.isEmpty()) {
                        return Mono.empty();
                    }

                    // Creating all half-hour intervals
                    List<HalfHourInterval> intervals = new ArrayList<>();
                    for (CarbonGenerationResponse resp : responses) {
                        if (resp.getData() != null) {
                            for (CarbonGenerationData data : resp.getData()) {
                                CarbonFuelMix mix = fromListToCarbonFuelMix(data.getGenerationMix());
                                double clean = EnergyCalculator.calculateCleanPercentage(mix);

                                intervals.add(new HalfHourInterval(data.getFrom(), data.getTo(), mix, clean));
                            }
                        }
                    }

                    int windowSize = hours * 2; // number of half-hour intervals
                    if (intervals.size() < windowSize) return Mono.empty();

                    // Sliding window: find the best window
                    CleanEnergyWindow bestWindow = null;
                    for (int i = 0; i <= intervals.size() - windowSize; i++) {
                        List<HalfHourInterval> window = intervals.subList(i, i + windowSize);
                        double avgClean = EnergyCalculator.calculateAverageCleanPercentage(window);

                        if (bestWindow == null || avgClean > bestWindow.getAverageCleanPercentage()) {
                            bestWindow = new CleanEnergyWindow(
                                    window.get(0).getFrom(),
                                    window.get(window.size() - 1).getTo(),
                                    avgClean
                            );
                        }
                    }

                    if (bestWindow == null) return Mono.empty();

                    OptimalChargingWindowResponse result = new OptimalChargingWindowResponse();
                    result.setStart(bestWindow.getStart());
                    result.setEnd(bestWindow.getEnd());
                    result.setAverageCleanPercentage(bestWindow.getAverageCleanPercentage());

                    return Mono.just(result);
                });
    }

    private CarbonFuelMix fromListToCarbonFuelMix(List<GenerationMixItem> items) {
        CarbonFuelMix mix = new CarbonFuelMix();
        if (items == null) return mix;

        for (GenerationMixItem item : items) {
            switch (item.getFuel().toLowerCase()) {
                case "biomass": mix.setBiomass(item.getPercentage()); break;
                case "nuclear": mix.setNuclear(item.getPercentage()); break;
                case "hydro":   mix.setHydro(item.getPercentage()); break;
                case "wind":    mix.setWind(item.getPercentage()); break;
                case "solar":   mix.setSolar(item.getPercentage()); break;
                case "gas":     mix.setGas(item.getPercentage()); break;
                case "coal":    mix.setCoal(item.getPercentage()); break;
                default:        mix.setOther(item.getPercentage()); break;
            }
        }

        return mix;
    }
}
