package com.example.GreenCharge.Planner.client;

import com.example.GreenCharge.Planner.model.carbon.CarbonGenerationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CarbonIntensityClientTest {

    private CarbonIntensityClient client;
    private WebClient webClientMock;

    @BeforeEach
    void setup() {
        webClientMock = mock(WebClient.class, RETURNS_DEEP_STUBS);
        client = new CarbonIntensityClient(webClientMock);
    }

    @Test
    void shouldReturnGenerationDataForSingleDay() {
        // given
        CarbonGenerationResponse mockResponse = new CarbonGenerationResponse();

        when(webClientMock.get()
                .uri(anyString())
                .retrieve()
                .bodyToMono(CarbonGenerationResponse.class))
                .thenReturn(Mono.just(mockResponse));

        ZonedDateTime from = ZonedDateTime.parse("2024-01-01T00:00:00Z");
        ZonedDateTime to   = ZonedDateTime.parse("2024-01-01T23:00:00Z");

        // when
        List<CarbonGenerationResponse> result =
                client.getGenerationDataMulti(from, to).block();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
