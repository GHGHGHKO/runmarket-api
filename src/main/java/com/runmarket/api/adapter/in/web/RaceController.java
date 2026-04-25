package com.runmarket.api.adapter.in.web;

import com.runmarket.api.adapter.in.web.dto.RaceDetailResponse;
import com.runmarket.api.adapter.in.web.dto.RaceListItemResponse;
import com.runmarket.api.adapter.in.web.dto.SaveRaceRequest;
import com.runmarket.api.domain.port.in.race.GetRaceUseCase;
import com.runmarket.api.domain.port.in.race.GetRacesUseCase;
import com.runmarket.api.domain.port.in.race.SaveRaceCommand;
import com.runmarket.api.domain.port.in.race.SaveRaceUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/races")
@RequiredArgsConstructor
public class RaceController {

    private final SaveRaceUseCase saveRaceUseCase;
    private final GetRacesUseCase getRacesUseCase;
    private final GetRaceUseCase getRaceUseCase;

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
        return ResponseEntity.ok(RaceDetailResponse.from(saveRaceUseCase.save(command)));
    }

    @GetMapping
    public ResponseEntity<List<RaceListItemResponse>> getRaces() {
        return ResponseEntity.ok(getRacesUseCase.getRaces().stream()
                .map(RaceListItemResponse::from)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RaceDetailResponse> getRace(@PathVariable UUID id) {
        return ResponseEntity.ok(RaceDetailResponse.from(getRaceUseCase.getRace(id)));
    }
}
