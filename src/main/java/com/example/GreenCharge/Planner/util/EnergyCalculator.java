package com.example.GreenCharge.Planner.util;

import com.example.GreenCharge.Planner.model.carbon.CarbonFuelMix;
import com.example.GreenCharge.Planner.model.carbon.GenerationMixItem;
import com.example.GreenCharge.Planner.model.domain.HalfHourInterval;

import java.util.List;

public class EnergyCalculator {

    /**
     * Oblicza procent czystej energii dla pojedynczego miksu.
     * Czyste źródła: biomass, nuclear, hydro, wind, solar
     */
    public static double calculateCleanPercentage(CarbonFuelMix mix) {
        if (mix == null) return 0.0;

        return safeDouble(mix.getBiomass())
                + safeDouble(mix.getNuclear())
                + safeDouble(mix.getHydro())
                + safeDouble(mix.getWind())
                + safeDouble(mix.getSolar());
    }

    /**
     * Oblicza średni procent czystej energii dla listy interwałów.
     * HalfHourInterval musi mieć wcześniej ustawione cleanPercentage.
     */
    public static double calculateAverageCleanPercentage(List<HalfHourInterval> intervals) {
        if (intervals == null || intervals.isEmpty()) return 0.0;

        double total = 0.0;
        for (HalfHourInterval interval : intervals) {
            total += interval.getCleanPercentage(); // tu działa jeśli HalfHourInterval ma getter i setter
        }

        return total / intervals.size();
    }

    /**
     * Bezpieczne pobranie wartości double, żeby uniknąć NPE
     */
    private static double safeDouble(Double value) {
        return value == null ? 0.0 : value;
    }
    public static CarbonFuelMix fromListToCarbonFuelMix(List<GenerationMixItem> items) {
        CarbonFuelMix mix = new CarbonFuelMix();
        if (items == null) return mix;

        for (GenerationMixItem item : items) {
            switch (item.getFuel().toLowerCase()) {
                case "biomass": mix.setBiomass(item.getPercentage()); break;
                case "nuclear": mix.setNuclear(item.getPercentage()); break;
                case "hydro":    mix.setHydro(item.getPercentage()); break;
                case "wind":     mix.setWind(item.getPercentage()); break;
                case "solar":    mix.setSolar(item.getPercentage()); break;
                case "gas":      mix.setGas(item.getPercentage()); break;
                case "coal":     mix.setCoal(item.getPercentage()); break;
                default:         mix.setOther(item.getPercentage()); break;
            }
        }

        return mix;
    }

}
