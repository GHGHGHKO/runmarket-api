package com.runmarket.api.adapter.in.web;

import com.runmarket.api.adapter.in.web.dto.RaceDetailResponse;
import com.runmarket.api.adapter.in.web.dto.RaceLikeResponse;
import com.runmarket.api.adapter.in.web.dto.RaceListItemResponse;
import com.runmarket.api.adapter.in.web.dto.SaveRaceRequest;
import com.runmarket.api.common.SecurityUtils;
import com.runmarket.api.domain.model.Race;
import com.runmarket.api.domain.port.in.race.GetRaceUseCase;
import com.runmarket.api.domain.port.in.race.GetRaceLikeCountUseCase;
import com.runmarket.api.domain.port.in.race.GetRaceLikeStatusUseCase;
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
import java.util.Set;
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
    private final GetRaceLikeStatusUseCase getRaceLikeStatusUseCase;

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
        String userEmail = SecurityUtils.currentUserEmail();
        long likeCount = getRaceLikeCountUseCase.getLikeCount(saved.getId());
        boolean isLiked = getRaceLikeStatusUseCase.isLiked(saved.getId(), userEmail);
        return ResponseEntity.ok(RaceDetailResponse.from(saved, likeCount, isLiked));
    }

    @GetMapping
    public ResponseEntity<List<RaceListItemResponse>> getRaces() {
        List<Race> races = getRacesUseCase.getRaces();
        List<UUID> raceIds = races.stream().map(Race::getId).toList();
        String userEmail = SecurityUtils.currentUserEmail();
        Map<UUID, Long> likeCounts = getRaceLikeCountUseCase.getLikeCounts(raceIds);
        Set<UUID> likedRaceIds = getRaceLikeStatusUseCase.getLikedRaceIds(raceIds, userEmail);
        return ResponseEntity.ok(races.stream()
                .map(race -> RaceListItemResponse.from(
                        race,
                        likeCounts.getOrDefault(race.getId(), 0L),
                        likedRaceIds.contains(race.getId())
                ))
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RaceDetailResponse> getRace(@PathVariable UUID id) {
        Race race = getRaceUseCase.getRace(id);
        String userEmail = SecurityUtils.currentUserEmail();
        long likeCount = getRaceLikeCountUseCase.getLikeCount(race.getId());
        boolean isLiked = getRaceLikeStatusUseCase.isLiked(race.getId(), userEmail);
        return ResponseEntity.ok(RaceDetailResponse.from(race, likeCount, isLiked));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<RaceLikeResponse> likeRace(@PathVariable UUID id) {
        String userEmail = SecurityUtils.currentUserEmail();
        likeRaceUseCase.like(id, userEmail);
        long likeCount = getRaceLikeCountUseCase.getLikeCount(id);
        return ResponseEntity.ok(new RaceLikeResponse(likeCount, true));
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<RaceLikeResponse> unlikeRace(@PathVariable UUID id) {
        String userEmail = SecurityUtils.currentUserEmail();
        unlikeRaceUseCase.unlike(id, userEmail);
        long likeCount = getRaceLikeCountUseCase.getLikeCount(id);
        return ResponseEntity.ok(new RaceLikeResponse(likeCount, false));
    }
}
