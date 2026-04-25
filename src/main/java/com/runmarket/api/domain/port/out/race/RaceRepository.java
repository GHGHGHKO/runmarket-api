package com.runmarket.api.domain.port.out.race;

import com.runmarket.api.domain.model.Race;

import java.util.List;
import java.util.Optional;

public interface RaceRepository {
    Race save(Race race);
    List<Race> findAll();
    Optional<Race> findByExternalId(Integer externalId);
}
