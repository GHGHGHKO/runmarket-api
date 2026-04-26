package com.runmarket.api.adapter.in.web;

import com.runmarket.api.adapter.in.web.dto.RaceDetailResponse;
import com.runmarket.api.adapter.in.web.dto.RaceListItemResponse;
import com.runmarket.api.adapter.in.web.dto.SaveRaceRequest;
import com.runmarket.api.common.SecurityUtils;
import com.runmarket.api.domain.model.Race;
import com.runmarket.api.domain.port.in.race.GetRaceUseCase;
import com.runmarket.api.domain.port.in.race.GetRaceLikeCountUseCase;
import com.runmarket.api.domain.port.in.race.GetRacesUseCase;
import com.runmarket.api.domain.port.in.race.LikeRaceUseCase;
import com.runmarket.api.domain.port.in.race.SaveRaceCommand;
import com.runmarket.api.domain.port.in.race.SaveRaceUseCase;
import com.runmarket.api.domain.port.in.race.UnlikeRaceUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/races")
@RequiredArgsConstructor
public class RaceController {

    private final SaveRaceUseCase saveRaceUseCase;
    private final GetRacesUseCase getRacesUseCase;
    private final GetRaceUseCase getRaceUseCase;
    private final LikeRaceUseCase likeRaceUseCase;
    private final UnlikeRaceUseCase unlikeRaceUseCase;
    private final GetRaceLikeCountUseCase getRaceLikeCountUseCase;

    @PutMapping("/{externalId}")
    public ResponseEntity<RaceDetailResponse> saveRace(
            @PathVariable Integer externalId,
            @Valid @RequestBody SaveRaceRequest request
    ) {
        SaveRaceCommand command = new SaveRaceCommand(
                externalId,
                request.name(),
                request.courses(),
                request.date(),
                request.startTime(),
                request.venue(),
                request.venueAddress(),
                request.region(),
                request.organizer(),
                request.representative(),
                request.phone(),
                request.email(),
                request.registrationStartDate(),
                request.registrationEndDate(),
                request.homepageUrl(),
                request.lat(),
                request.lng(),
                request.description()
        );
        Race saved = saveRaceUseCase.save(command);
        long likeCount = getRaceLikeCountUseCase.getLikeCount(saved.getId());
        return ResponseEntity.ok(RaceDetailResponse.from(saved, likeCount));
    }

    @GetMapping
    public ResponseEntity<List<RaceListItemResponse>> getRaces() {
        List<Race> races = getRacesUseCase.getRaces();
        List<UUID> raceIds = races.stream().map(Race::getId).toList();
        Map<UUID, Long> likeCounts = getRaceLikeCountUseCase.getLikeCounts(raceIds);
        return ResponseEntity.ok(races.stream()
                .map(race -> RaceListItemResponse.from(race, likeCounts.getOrDefault(race.getId(), 0L)))
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RaceDetailResponse> getRace(@PathVariable UUID id) {
        Race race = getRaceUseCase.getRace(id);
        long likeCount = getRaceLikeCountUseCase.getLikeCount(race.getId());
        return ResponseEntity.ok(RaceDetailResponse.from(race, likeCount));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeRace(@PathVariable UUID id) {
        likeRaceUseCase.like(id, SecurityUtils.currentUserEmail());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlikeRace(@PathVariable UUID id) {
        unlikeRaceUseCase.unlike(id, SecurityUtils.currentUserEmail());
        return ResponseEntity.noContent().build();
    }
}
