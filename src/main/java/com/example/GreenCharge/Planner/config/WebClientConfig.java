package com.example.GreenCharge.Planner.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://api.carbonintensity.org.uk")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                // We add the ACCEPT header because some APIs require it to return data correctly
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(10)) // Time fo answer
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // Time for connect
                ))
                .build();
    }
}
/*    This class creates a common HTTP client (WebClient) for the entire project.
       It sets the default Carbon Intensity API URL → easy endpoint invocation.
       It adds a default JSON Content-Type header.
       It configures secure timeouts:
       -10 seconds for connection
       -10 seconds for response
       This ensures the client is ready for use in services and doesn't hang the application
       if the API is slow or unavailable.*/