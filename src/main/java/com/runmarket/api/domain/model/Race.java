package com.runmarket.api.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class Race {
    private UUID id;
    private Integer externalId;
    private String name;
    private List<String> courses;
    private LocalDate date;
    private String startTime;
    private String venue;
    private String venueAddress;
    private String region;
    private String organizer;
    private String representative;
    private String phone;
    private String email;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private String homepageUrl;
    private Double lat;
    private Double lng;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RegistrationStatus getRegistrationStatus() {
        return RegistrationStatus.of(registrationStartDate, registrationEndDate);
    }
}
