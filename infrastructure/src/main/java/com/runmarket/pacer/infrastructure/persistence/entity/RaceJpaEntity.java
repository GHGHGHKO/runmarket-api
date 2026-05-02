package com.runmarket.pacer.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "races")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaceJpaEntity extends BaseEntity {

    @Column(nullable = false, unique = true, updatable = false)
    private Integer externalId;

    @Column(nullable = false)
    private String name;

    private String courses;

    @Column(nullable = false)
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

    @Column(columnDefinition = "TEXT")
    private String description;

    public void update(String name, String courses, LocalDate date, String startTime,
                       String venue, String venueAddress, String region, String organizer,
                       String representative, String phone, String email,
                       LocalDate registrationStartDate, LocalDate registrationEndDate,
                       String homepageUrl, Double lat, Double lng, String description) {
        this.name = name;
        this.courses = courses;
        this.date = date;
        this.startTime = startTime;
        this.venue = venue;
        this.venueAddress = venueAddress;
        this.region = region;
        this.organizer = organizer;
        this.representative = representative;
        this.phone = phone;
        this.email = email;
        this.registrationStartDate = registrationStartDate;
        this.registrationEndDate = registrationEndDate;
        this.homepageUrl = homepageUrl;
        this.lat = lat;
        this.lng = lng;
        this.description = description;
    }
}
