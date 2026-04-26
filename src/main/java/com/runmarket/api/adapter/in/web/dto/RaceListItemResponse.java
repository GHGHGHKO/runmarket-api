package com.runmarket.api.adapter.in.web.dto;

import com.runmarket.api.domain.model.Race;
import com.runmarket.api.domain.model.RegistrationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RaceListItemResponse(
        UUID id,
        String name,
        List<String> courses,
        LocalDate date,
        String venue,
        String region,
        String organizer,
        String phone,
        RegistrationStatus registrationStatus,
        long likeCount,
        boolean isLiked
) {
    public static RaceListItemResponse from(Race race, long likeCount, boolean isLiked) {
        return new RaceListItemResponse(
                race.getId(),
                race.getName(),
                race.getCourses(),
                race.getDate(),
                race.getVenue(),
                race.getRegion(),
                race.getOrganizer(),
                race.getPhone(),
                race.getRegistrationStatus(),
                likeCount,
                isLiked
        );
    }
}
