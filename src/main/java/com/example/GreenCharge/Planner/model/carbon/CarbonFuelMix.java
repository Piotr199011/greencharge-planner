package com.example.GreenCharge.Planner.model.carbon;

public class CarbonFuelMix {
    private Double biomass=0.0;
    private Double nuclear=0.0;
    private Double hydro=0.0;
    private Double wind=0.0;
    private Double solar=0.0;
    private Double gas=0.0;
    private Double coal=0.0;
    private Double other=0.0;

    public Double getBiomass() {
        return biomass;
    }

    public void setBiomass(Double biomass) {
        this.biomass = biomass;
    }

    public Double getNuclear() {
        return nuclear;
    }

    public void setNuclear(Double nuclear) {
        this.nuclear = nuclear;
    }

    public Double getHydro() {
        return hydro;
    }

    public void setHydro(Double hydro) {
        this.hydro = hydro;
    }

    public Double getWind() {
        return wind;
    }

    public void setWind(Double wind) {
        this.wind = wind;
    }

    public Double getSolar() {
        return solar;
    }

    public void setSolar(Double solar) {
        this.solar = solar;
    }

    public Double getGas() {
        return gas;
    }

    public void setGas(Double gas) {
        this.gas = gas;
    }

    public Double getCoal() {
        return coal;
    }

    public void setCoal(Double coal) {
        this.coal = coal;
    }

    public Double getOther() {
        return other;
    }

    public void setOther(Double other) {
        this.other = other;
    }
}
    /*is responsible for describing where the energy came from during
    a given time period and in what percentage*/