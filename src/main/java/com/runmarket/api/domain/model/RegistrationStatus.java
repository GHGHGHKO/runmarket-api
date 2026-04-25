package com.runmarket.api.domain.model;

import java.time.LocalDate;

public enum RegistrationStatus {
    UPCOMING, OPEN, CLOSED;

    public static RegistrationStatus of(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return CLOSED;
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) {
            return UPCOMING;
        }
        if (today.isAfter(endDate)) {
            return CLOSED;
        }
        return OPEN;
    }
}
