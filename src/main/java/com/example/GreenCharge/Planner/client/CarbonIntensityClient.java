package com.example.GreenCharge.Planner.client;

import com.example.GreenCharge.Planner.model.carbon.CarbonGenerationResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarbonIntensityClient {

    private final WebClient webClient;

    // API requires format ISO8601
    private static final DateTimeFormatter API_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'");

    public CarbonIntensityClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<List<CarbonGenerationResponse>> getGenerationDataMulti(ZonedDateTime from, ZonedDateTime to) {
        ZonedDateTime utcFrom = roundToSettlement(from.withZoneSameInstant(ZoneOffset.UTC));
        ZonedDateTime utcTo = roundToSettlement(to.withZoneSameInstant(ZoneOffset.UTC));

        List<Mono<CarbonGenerationResponse>> calls = new ArrayList<>();
        ZonedDateTime currentStart = utcFrom;

        while (currentStart.isBefore(utcTo)) {
            ZonedDateTime currentEnd = currentStart.plusHours(24);
            if (currentEnd.isAfter(utcTo)) {
                currentEnd = utcTo;
            }

            if (!currentStart.isBefore(currentEnd)) {
                break;
            }

            String fromStr = currentStart.format(API_FORMATTER);
            String toStr = currentEnd.format(API_FORMATTER);

            String uri = UriComponentsBuilder.fromPath("/generation/{from}/{to}")
                    .buildAndExpand(fromStr, toStr)
                    .toUriString();

            calls.add(webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(CarbonGenerationResponse.class)
                    .doOnSubscribe(sub -> System.out.println("Downloading data: " + fromStr + " to " + toStr))
                    .onErrorResume(e -> {
                        System.err.println(" Error API Carbon Intensity from scope " + fromStr + "-" + toStr + ": " + e.getMessage());
                        return Mono.empty();
                    }));

            currentStart = currentEnd;
        }

        return Flux.mergeSequential(calls).collectList();
    }

    private ZonedDateTime roundToSettlement(ZonedDateTime time) {
        // API operates on intervals 30-minuts
        int minute = time.getMinute() < 30 ? 0 : 30;
        return time.withMinute(minute).withSecond(0).withNano(0);
    }
}
