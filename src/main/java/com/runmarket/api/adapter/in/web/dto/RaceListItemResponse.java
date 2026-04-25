package com.runmarket.api.adapter.in.web.dto;

import com.runmarket.api.domain.model.Race;
import com.runmarket.api.domain.model.RegistrationStatus;

import java.time.LocalDate;
import java.util.List;

public record RaceListItemResponse(
        Integer id,
        String name,
        List<String> courses,
        LocalDate date,
        String venue,
        String region,
        String organizer,
        String phone,
        RegistrationStatus registrationStatus
) {
    public static RaceListItemResponse from(Race race) {
        return new RaceListItemResponse(
                race.getExternalId(),
                race.getName(),
                race.getCourses(),
                race.getDate(),
                race.getVenue(),
                race.getRegion(),
                race.getOrganizer(),
                race.getPhone(),
                race.getRegistrationStatus()
        );
    }
}
