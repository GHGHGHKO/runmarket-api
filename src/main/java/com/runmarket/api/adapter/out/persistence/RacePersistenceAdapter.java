package com.runmarket.api.adapter.out.persistence;

import com.runmarket.api.adapter.out.persistence.entity.RaceJpaEntity;
import com.runmarket.api.adapter.out.persistence.mapper.RaceMapper;
import com.runmarket.api.adapter.out.persistence.repository.RaceJpaRepository;
import com.runmarket.api.domain.model.Race;
import com.runmarket.api.domain.port.out.race.RaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RacePersistenceAdapter implements RaceRepository {

    private final RaceJpaRepository raceJpaRepository;
    private final RaceMapper raceMapper;

    @Override
    public Race save(Race race) {
        RaceJpaEntity entity = raceJpaRepository.findByExternalId(race.getExternalId())
                .map(existing -> {
                    existing.update(
                            race.getName(),
                            raceMapper.joinCourses(race.getCourses()),
                            race.getDate(),
                            race.getStartTime(),
                            race.getVenue(),
                            race.getVenueAddress(),
                            race.getRegion(),
                            race.getOrganizer(),
                            race.getRepresentative(),
                            race.getPhone(),
                            race.getEmail(),
                            race.getRegistrationStartDate(),
                            race.getRegistrationEndDate(),
                            race.getHomepageUrl(),
                            race.getLat(),
                            race.getLng(),
                            race.getDescription()
                    );
                    return existing;
                })
                .orElseGet(() -> raceMapper.toJpaEntity(race));
        return raceMapper.toDomain(raceJpaRepository.save(entity));
    }

    @Override
    public List<Race> findAll() {
        return raceJpaRepository.findAll().stream()
                .map(raceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Race> findById(UUID id) {
        return raceJpaRepository.findById(id).map(raceMapper::toDomain);
    }

    @Override
    public Optional<Race> findByExternalId(Integer externalId) {
        return raceJpaRepository.findByExternalId(externalId).map(raceMapper::toDomain);
    }
}
