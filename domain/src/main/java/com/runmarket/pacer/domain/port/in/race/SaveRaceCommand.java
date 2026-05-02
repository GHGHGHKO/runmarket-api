package com.runmarket.pacer.domain.port.in.race;

import java.time.LocalDate;
import java.util.List;

public record SaveRaceCommand(
        Integer externalId,
        String name,
        List<String> courses,
        LocalDate date,
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
