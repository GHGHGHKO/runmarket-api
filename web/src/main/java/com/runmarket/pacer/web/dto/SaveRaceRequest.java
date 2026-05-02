package com.runmarket.pacer.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record SaveRaceRequest(
        @NotBlank String name,
        List<String> courses,
        @NotNull LocalDate date,
        String startTime,
        String venue,
        String venueAddress,
        String region,
        String organizer,
        String representative,
        String phone,
        String email,
        LocalDate registrationStartDate,
        LocalDate registrationEndDate,
        String homepageUrl,
        Double lat,
        Double lng,
        String description
) {}
