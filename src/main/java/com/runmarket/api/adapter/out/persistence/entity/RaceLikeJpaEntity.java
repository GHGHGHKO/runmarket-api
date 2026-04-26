package com.runmarket.api.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "race_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "race_id"})
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaceLikeJpaEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "race_id", nullable = false, updatable = false)
    private UUID raceId;
}
