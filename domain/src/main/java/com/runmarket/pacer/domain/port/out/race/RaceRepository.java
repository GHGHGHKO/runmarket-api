package com.runmarket.pacer.domain.port.out.race;

import com.runmarket.pacer.domain.model.Race;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RaceRepository {
    Race save(Race race);
    List<Race> findAll();
    Optional<Race> findById(UUID id);
    Optional<Race> findByExternalId(Integer externalId);
    List<Race> findAllByIds(List<UUID> ids);
}
