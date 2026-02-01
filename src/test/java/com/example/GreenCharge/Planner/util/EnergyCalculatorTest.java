package com.example.GreenCharge.Planner.util;

import com.example.GreenCharge.Planner.model.carbon.CarbonFuelMix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnergyCalculatorTest {

    @Test
    void shouldReturn100PercentForOnlyCleanSources() {

        CarbonFuelMix mix = new CarbonFuelMix();
        mix.setBiomass(20.0);
        mix.setNuclear(20.0);
        mix.setHydro(20.0);
        mix.setWind(20.0);
        mix.setSolar(20.0);


        double result = EnergyCalculator.calculateCleanPercentage(mix);


        assertEquals(100.0, result, 0.001);
    }

    @Test
    void shouldReturn0PercentWhenNoCleanSourcesPresent() {

        CarbonFuelMix mix = new CarbonFuelMix();
        mix.setCoal(50.0);
        mix.setGas(30.0);
        mix.setOther(20.0);

        double result = EnergyCalculator.calculateCleanPercentage(mix);

        assertEquals(0.0, result, 0.001);
    }

    @Test
    void shouldReturnCorrectPercentageForMixedEnergyMix() {

        CarbonFuelMix mix = new CarbonFuelMix();
        mix.setWind(25.0);
        mix.setSolar(15.0);
        mix.setGas(40.0);
        mix.setCoal(20.0);

        double result = EnergyCalculator.calculateCleanPercentage(mix);


        assertEquals(40.0, result, 0.001);
    }

    @Test
    void shouldHandleNullValuesSafely() {

        CarbonFuelMix mix = new CarbonFuelMix();
        mix.setWind(null);
        mix.setSolar(10.0);


        double result = EnergyCalculator.calculateCleanPercentage(mix);


        assertEquals(10.0, result, 0.001);
    }

    @Test
    void shouldReturn0WhenMixIsNull() {

        double result = EnergyCalculator.calculateCleanPercentage(null);

        assertEquals(0.0, result, 0.001);
    }
}
