package com.example.GreenCharge.Planner.model.domain;

import java.time.ZonedDateTime;

public class CleanEnergyWindow {
    private ZonedDateTime start;
    private ZonedDateTime end;
    private double averageCleanPercentage;

    public CleanEnergyWindow(ZonedDateTime start, ZonedDateTime end, double averageCleanPercentage) {
        this.start = start;
        this.end = end;
        this.averageCleanPercentage = averageCleanPercentage;
    }

    public ZonedDateTime getStart() { return start; }
    public ZonedDateTime getEnd() { return end; }
    public double getAverageCleanPercentage() { return averageCleanPercentage; }
}
