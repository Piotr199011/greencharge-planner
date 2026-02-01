package com.example.GreenCharge.Planner.controller;

import com.example.GreenCharge.Planner.model.dto.OptimalChargingWindowResponse;
import com.example.GreenCharge.Planner.service.ChargingWindowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class ChargingWindowController {

    private final ChargingWindowService chargingWindowService;

    public ChargingWindowController(ChargingWindowService chargingWindowService) {
        this.chargingWindowService = chargingWindowService;
    }

    @GetMapping("/optimal-charging")
    public Mono<ResponseEntity<OptimalChargingWindowResponse>> getOptimalChargingWindow(
            @RequestParam int hours
    ) {
        if (hours < 1 || hours > 6) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return chargingWindowService.findOptimalChargingWindow(hours)
                .map(ResponseEntity::ok)         // if the result is available-> 200 OK
                .switchIfEmpty(Mono.just(ResponseEntity.noContent().build())); // if null -> 204 NO_CONTENT
    }
}
