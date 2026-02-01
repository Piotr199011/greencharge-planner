package com.example.GreenCharge.Planner.model.dto;

import java.time.LocalDate;

public class DailyAverageMixResponse {
    private LocalDate date;

    private DailyFuelMix averageMix;


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public DailyFuelMix getAverageMix() {
        return averageMix;
    }

    public void setAverageMix(DailyFuelMix averageMix) {
        this.averageMix = averageMix;
    }

    public double getCleanPercentage() {
        return cleanPercentage;
    }

    public void setCleanPercentage(double cleanPercentage) {
        this.cleanPercentage = cleanPercentage;
    }

    private double cleanPercentage;
}
