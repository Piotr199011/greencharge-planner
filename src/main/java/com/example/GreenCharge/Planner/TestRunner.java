package com.example.GreenCharge.Planner;

import com.example.GreenCharge.Planner.client.CarbonIntensityClient;
import com.example.GreenCharge.Planner.model.carbon.CarbonFuelMix;
import com.example.GreenCharge.Planner.model.carbon.CarbonGenerationData;
import com.example.GreenCharge.Planner.model.carbon.CarbonGenerationResponse;
import com.example.GreenCharge.Planner.util.EnergyCalculator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static com.example.GreenCharge.Planner.util.EnergyCalculator.fromListToCarbonFuelMix;

@Component
public class TestRunner implements CommandLineRunner {

    private final CarbonIntensityClient client;

    public TestRunner(CarbonIntensityClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) {
        try {
            ZonedDateTime from = ZonedDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            ZonedDateTime to = ZonedDateTime.now();


            List<CarbonGenerationResponse> responses = client.getGenerationDataMulti(from, to)
                    .timeout(Duration.ofSeconds(10))
                    .onErrorReturn(Collections.emptyList())
                    .block();

            if (responses == null || responses.isEmpty()) {
                System.out.println("No API data for the specified period");
                return;
            }

            int totalRecords = 0;

            for (CarbonGenerationResponse response : responses) {
                if (response.getData() != null) {
                    totalRecords += response.getData().size();

                    for (CarbonGenerationData data : response.getData()) {
                        // Converting the GenerationMixItem → CarbonFuelMix list
                        CarbonFuelMix mix = fromListToCarbonFuelMix(data.getGenerationMix());
                        double clean = EnergyCalculator.calculateCleanPercentage(mix);

                        System.out.printf(
                                "Time period: %s - %s | Biomass: %.1f%%, Nuclear: %.1f%%, Hydro: %.1f%%," +
                                        " Wind: %.1f%%, Solar: %.1f%%, Gas: %.1f%%, Coal: %.1f%%," +
                                        " Other: %.1f%% | Clean energy: %.1f%%%n",
                                data.getFrom(), data.getTo(),
                                mix.getBiomass(),
                                mix.getNuclear(),
                                mix.getHydro(),
                                mix.getWind(),
                                mix.getSolar(),
                                mix.getGas(),
                                mix.getCoal(),
                                mix.getOther(),
                                clean
                        );
                    }
                }
            }

            System.out.println("---------------------------------------------------");
            System.out.println("Number of records in the entire period: " + totalRecords);

        } catch (Exception e) {
            System.err.println("TestRunner failed: " + e.getMessage());
        }
    }
}

