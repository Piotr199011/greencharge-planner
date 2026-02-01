package com.example.GreenCharge.Planner.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/*
  This class is responsible for configuring the serialization and deserialization of dates and times in Spring Boot.
   It supports LocalDateTime and ZonedDateTime, and sets its own format and time zone.
 */
@Configuration
public class JacksonConfig {

    private static final String LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String ZONED_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        DateTimeFormatter localFormatter = DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_PATTERN);
        DateTimeFormatter zonedFormatter = DateTimeFormatter.ofPattern(ZONED_DATE_TIME_PATTERN);

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // LocalDateTime
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(localFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(localFormatter));

        // ZonedDateTime
        javaTimeModule.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(zonedFormatter));
        javaTimeModule.addDeserializer(ZonedDateTime.class, InstantDeserializer.ZONED_DATE_TIME);

        // Module registration in ObjectMapper
        objectMapper.registerModule(javaTimeModule);

        // Disabling saving dates as timestamps (readable JSON)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }
}
