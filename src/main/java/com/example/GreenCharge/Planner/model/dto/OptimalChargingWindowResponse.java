package com.example.GreenCharge.Planner.model.dto;

import java.time.ZonedDateTime;

public class OptimalChargingWindowResponse {
    private ZonedDateTime start;
    private ZonedDateTime end;
    private double averageCleanPercentage;

    public ZonedDateTime getStart() { return start; }
    public void setStart(ZonedDateTime start) { this.start = start; }

    public ZonedDateTime getEnd() { return end; }
    public void setEnd(ZonedDateTime end) { this.end = end; }

    public double getAverageCleanPercentage() { return averageCleanPercentage; }
    public void setAverageCleanPercentage(double averageCleanPercentage) { this.averageCleanPercentage = averageCleanPercentage; }
}
