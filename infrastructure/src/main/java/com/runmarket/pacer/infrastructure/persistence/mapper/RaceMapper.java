package com.runmarket.pacer.infrastructure.persistence.mapper;

import com.runmarket.pacer.domain.model.Race;
import com.runmarket.pacer.infrastructure.persistence.entity.RaceJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class RaceMapper {

    private static final String COURSE_DELIMITER = ",";

    public Race toDomain(RaceJpaEntity entity) {
        return Race.builder()
                .id(entity.getId())
                .externalId(entity.getExternalId())
                .name(entity.getName())
                .courses(parseCourses(entity.getCourses()))
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .venue(entity.getVenue())
                .venueAddress(entity.getVenueAddress())
                .region(entity.getRegion())
                .organizer(entity.getOrganizer())
                .representative(entity.getRepresentative())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .registrationStartDate(entity.getRegistrationStartDate())
                .registrationEndDate(entity.getRegistrationEndDate())
                .homepageUrl(entity.getHomepageUrl())
                .lat(entity.getLat())
                .lng(entity.getLng())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public RaceJpaEntity toJpaEntity(Race race) {
        return RaceJpaEntity.builder()
                .externalId(race.getExternalId())
                .name(race.getName())
                .courses(joinCourses(race.getCourses()))
                .date(race.getDate())
                .startTime(race.getStartTime())
                .venue(race.getVenue())
                .venueAddress(race.getVenueAddress())
                .region(race.getRegion())
                .organizer(race.getOrganizer())
                .representative(race.getRepresentative())
                .phone(race.getPhone())
                .email(race.getEmail())
                .registrationStartDate(race.getRegistrationStartDate())
                .registrationEndDate(race.getRegistrationEndDate())
                .homepageUrl(race.getHomepageUrl())
                .lat(race.getLat())
                .lng(race.getLng())
                .description(race.getDescription())
                .build();
    }

    private List<String> parseCourses(String courses) {
        if (courses == null || courses.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(courses.split(COURSE_DELIMITER))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public String joinCourses(List<String> courses) {
        if (courses == null || courses.isEmpty()) {
            return null;
        }
        return String.join(COURSE_DELIMITER, courses);
    }
}
