package com.example.GreenCharge.Planner.model.carbon;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.List;

public class CarbonGenerationData {

    private ZonedDateTime from;
    private ZonedDateTime to;

    @JsonProperty("generationmix")
    private List<GenerationMixItem> generationMix; //now JSON list

    public ZonedDateTime getFrom() { return from; }
    public void setFrom(ZonedDateTime from) { this.from = from; }

    public ZonedDateTime getTo() { return to; }
    public void setTo(ZonedDateTime to) { this.to = to; }

    public List<GenerationMixItem> getGenerationMix() { return generationMix; }
    public void setGenerationMix(List<GenerationMixItem> generationMix) { this.generationMix = generationMix; }

}
