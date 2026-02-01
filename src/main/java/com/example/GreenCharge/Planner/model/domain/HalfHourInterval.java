package com.example.GreenCharge.Planner.model.domain;

import com.example.GreenCharge.Planner.model.carbon.CarbonFuelMix;

import java.time.ZonedDateTime;

public class HalfHourInterval {
    public ZonedDateTime from;

    public ZonedDateTime to;

    private CarbonFuelMix fuelMix;

    private double cleanPercentage;

    public HalfHourInterval(ZonedDateTime from, ZonedDateTime to, CarbonFuelMix fuelMix, double cleanPercentage) {
        this.from = from;
        this.to = to;
        this.fuelMix = fuelMix;
        this.cleanPercentage = cleanPercentage;
    }
    public HalfHourInterval(ZonedDateTime from, ZonedDateTime to, CarbonFuelMix fuelMix) {
        this(from, to, fuelMix, 0.0);
    }
    public ZonedDateTime getFrom() {
        return from;
    }

    public void setFrom(ZonedDateTime from) {
        this.from = from;
    }

    public ZonedDateTime getTo() {
        return to;
    }

    public void setTo(ZonedDateTime to) {
        this.to = to;
    }

    public CarbonFuelMix getFuelMix() {
        return fuelMix;
    }

    public void setFuelMix(CarbonFuelMix fuelMix) {
        this.fuelMix = fuelMix;
    }

    public double getCleanPercentage() {
        return cleanPercentage;
    }

    public void setCleanPercentage(double cleanPercentage) {
        this.cleanPercentage = cleanPercentage;
    }
}
