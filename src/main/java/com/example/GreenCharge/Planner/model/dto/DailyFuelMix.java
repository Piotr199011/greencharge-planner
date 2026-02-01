package com.example.GreenCharge.Planner.model.dto;

public class DailyFuelMix {
    private double biomass;

    private double nuclear;

    private double hydro;

    private double wind;

    private double solar;

    private double gas;

    private double coal;

    private double other;

    public double getBiomass() {
        return biomass;
    }

    public void setBiomass(double biomass) {
        this.biomass = biomass;
    }

    public double getNuclear() {
        return nuclear;
    }

    public void setNuclear(double nuclear) {
        this.nuclear = nuclear;
    }

    public double getHydro() {
        return hydro;
    }

    public void setHydro(double hydro) {
        this.hydro = hydro;
    }

    public double getWind() {
        return wind;
    }

    public void setWind(double wind) {
        this.wind = wind;
    }

    public double getSolar() {
        return solar;
    }

    public void setSolar(double solar) {
        this.solar = solar;
    }

    public double getGas() {
        return gas;
    }

    public void setGas(double gas) {
        this.gas = gas;
    }

    public double getCoal() {
        return coal;
    }

    public void setCoal(double coal) {
        this.coal = coal;
    }

    public double getOther() {
        return other;
    }

    public void setOther(double other) {
        this.other = other;
    }
}
