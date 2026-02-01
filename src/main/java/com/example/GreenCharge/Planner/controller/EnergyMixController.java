package com.example.GreenCharge.Planner.controller;

import com.example.GreenCharge.Planner.model.dto.DailyAverageMixResponse;
import com.example.GreenCharge.Planner.service.EnergyMixService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class EnergyMixController {

    private final EnergyMixService energyMixService;

    public EnergyMixController(EnergyMixService energyMixService) {
        this.energyMixService = energyMixService;
    }

    @GetMapping("/api/energy-mix")
    public Mono<List<DailyAverageMixResponse>> getEnergyMix() {
        return energyMixService.getDailyAverageMixForThreeDays();
    }
}
