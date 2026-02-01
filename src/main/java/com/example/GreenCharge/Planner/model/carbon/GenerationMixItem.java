package com.example.GreenCharge.Planner.model.carbon;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenerationMixItem {

    private String fuel;


    @JsonProperty("perc")
    private double percentage;

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
