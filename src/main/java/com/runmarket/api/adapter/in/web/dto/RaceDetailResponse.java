package com.runmarket.api.adapter.in.web.dto;

import com.runmarket.api.domain.model.Race;
import com.runmarket.api.domain.model.RegistrationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RaceDetailResponse(
        UUID id,
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
        RegistrationStatus registrationStatus,
        String homepageUrl,
        Double lat,
        Double lng,
        String description
) {
    public static RaceDetailResponse from(Race race) {
        return new RaceDetailResponse(
                race.getId(),
                race.getName(),
                race.getCourses(),
                race.getDate(),
                race.getStartTime(),
                race.getVenue(),
                race.getVenueAddress(),
                race.getRegion(),
                race.getOrganizer(),
                race.getRepresentative(),
                race.getPhone(),
                race.getEmail(),
                race.getRegistrationStartDate(),
                race.getRegistrationEndDate(),
                race.getRegistrationStatus(),
                race.getHomepageUrl(),
                race.getLat(),
                race.getLng(),
                race.getDescription()
        );
    }
}
