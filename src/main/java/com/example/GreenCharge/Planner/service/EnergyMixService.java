package com.example.GreenCharge.Planner.service;

import com.example.GreenCharge.Planner.client.CarbonIntensityClient;
import com.example.GreenCharge.Planner.model.carbon.CarbonGenerationData;
import com.example.GreenCharge.Planner.model.carbon.CarbonGenerationResponse;
import com.example.GreenCharge.Planner.model.carbon.CarbonFuelMix;
import com.example.GreenCharge.Planner.model.carbon.GenerationMixItem;
import com.example.GreenCharge.Planner.model.domain.HalfHourInterval;
import com.example.GreenCharge.Planner.model.dto.DailyAverageMixResponse;
import com.example.GreenCharge.Planner.model.dto.DailyFuelMix;
import com.example.GreenCharge.Planner.util.EnergyCalculator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EnergyMixService {

    private final CarbonIntensityClient carbonClient;

    public EnergyMixService(CarbonIntensityClient carbonClient) {
        this.carbonClient = carbonClient;
    }

    public Mono<List<DailyAverageMixResponse>> getDailyAverageMixForThreeDays() {

        ZonedDateTime from = ZonedDateTime.now()
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        ZonedDateTime to = from.plusDays(3).minusSeconds(1);

        return carbonClient.getGenerationDataMulti(from, to)
                .map(responses -> {

                    if (responses == null || responses.isEmpty()) {
                        return Collections.<DailyAverageMixResponse>emptyList();
                    }

                    //  Converting data to HalfHourInterval
                    List<HalfHourInterval> intervals = new ArrayList<>();

                    for (CarbonGenerationResponse resp : responses) {
                        if (resp.getData() == null) continue;

                        for (CarbonGenerationData data : resp.getData()) {

                            CarbonFuelMix mix =
                                    fromListToCarbonFuelMix(data.getGenerationMix());

                            double clean =
                                    EnergyCalculator.calculateCleanPercentage(mix);

                            intervals.add(new HalfHourInterval(
                                    data.getFrom(),
                                    data.getTo(),
                                    mix,
                                    clean
                            ));
                        }
                    }

                    //  Grouping by days
                    Map<LocalDate, List<HalfHourInterval>> intervalsByDate =
                            intervals.stream()
                                    .collect(Collectors.groupingBy(
                                            i -> i.getFrom().toLocalDate()
                                    ));

                    // Calculating daily averages
                    List<DailyAverageMixResponse> dailyResponses = new ArrayList<>();

                    for (Map.Entry<LocalDate, List<HalfHourInterval>> entry : intervalsByDate.entrySet()) {

                        LocalDate date = entry.getKey();
                        List<HalfHourInterval> dayIntervals = entry.getValue();

                        DailyFuelMix avgMix = calculateAverageMix(dayIntervals);
                        double avgClean =
                                EnergyCalculator.calculateAverageCleanPercentage(dayIntervals);

                        DailyAverageMixResponse response =
                                new DailyAverageMixResponse();

                        response.setDate(date);
                        response.setAverageMix(avgMix);
                        response.setCleanPercentage(avgClean);

                        dailyResponses.add(response);
                    }

                    dailyResponses.sort(
                            Comparator.comparing(DailyAverageMixResponse::getDate)
                    );

                    return dailyResponses;
                });
    }

    private DailyFuelMix calculateAverageMix(List<HalfHourInterval> intervals) {

        DailyFuelMix mix = new DailyFuelMix();

        mix.setBiomass(intervals.stream()
                .mapToDouble(i -> i.getFuelMix().getBiomass())
                .average().orElse(0));

        mix.setNuclear(intervals.stream()
                .mapToDouble(i -> i.getFuelMix().getNuclear())
                .average().orElse(0));

        mix.setHydro(intervals.stream()
                .mapToDouble(i -> i.getFuelMix().getHydro())
                .average().orElse(0));

        mix.setWind(intervals.stream()
                .mapToDouble(i -> i.getFuelMix().getWind())
                .average().orElse(0));

        mix.setSolar(intervals.stream()
                .mapToDouble(i -> i.getFuelMix().getSolar())
                .average().orElse(0));

        mix.setGas(intervals.stream()
                .mapToDouble(i -> i.getFuelMix().getGas())
                .average().orElse(0));

        mix.setCoal(intervals.stream()
                .mapToDouble(i -> i.getFuelMix().getCoal())
                .average().orElse(0));

        mix.setOther(intervals.stream()
                .mapToDouble(i -> i.getFuelMix().getOther())
                .average().orElse(0));

        return mix;
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
