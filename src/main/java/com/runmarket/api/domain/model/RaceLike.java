package com.runmarket.api.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class RaceLike {
    private UUID id;
    private UUID userId;
    private UUID raceId;
    private LocalDateTime createdAt;
}
