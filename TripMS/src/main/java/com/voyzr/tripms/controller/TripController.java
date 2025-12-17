package com.voyzr.tripms.controller;

import com.voyzr.tripms.dto.TripDTO;
import com.voyzr.tripms.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
@Tag(name = "Trips", description = "CRUD operations for trips")
@Slf4j
@Validated
public class TripController {

    private final TripService tripService;

    @Operation(summary = "Create a new trip")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripDTO create(@Valid @RequestBody TripDTO tripDTO) {
        log.info("POST /api/v1/trips - Creating trip with name='{}'", tripDTO != null ? tripDTO.getName() : null);
        TripDTO created = tripService.createTrip(tripDTO);
        log.debug("Created trip id={} status={}", created.getId(), created.getStatus());
        return created;
    }

    @Operation(summary = "Get a trip by ID")
    @GetMapping("/{id}")
    public TripDTO getById(@PathVariable @Positive(message = "id must be positive") Long id) {
        log.info("GET /api/v1/trips/{} - Fetching trip", id);
        try {
            TripDTO dto = tripService.getTrip(id);
            log.debug("Found trip id={} name='{}'", dto.getId(), dto.getName());
            return dto;
        } catch (RuntimeException ex) {
            log.warn("Trip not found for id={}: {}", id, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @Operation(summary = "List all trips")
    @GetMapping
    public List<TripDTO> getAll() {
        log.info("GET /api/v1/trips - Listing trips");
        List<TripDTO> list = tripService.getAllTrips();
        log.debug("Returned {} trips", list.size());
        return list;
    }

    @Operation(summary = "Update a trip by ID")
    @PutMapping("/{id}")
    public TripDTO update(@PathVariable @Positive(message = "id must be positive") Long id,
                          @Valid @RequestBody TripDTO tripDTO) {
        log.info("PUT /api/v1/trips/{} - Updating trip", id);
        try {
            TripDTO updated = tripService.updateTrip(id, tripDTO);
            log.debug("Updated trip id={} name='{}'", updated.getId(), updated.getName());
            return updated;
        } catch (RuntimeException ex) {
            log.warn("Trip not found for update id={}: {}", id, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @Operation(summary = "Delete a trip by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive(message = "id must be positive") Long id) {
        log.info("DELETE /api/v1/trips/{} - Deleting trip", id);
        try {
            tripService.deleteTrip(id);
            log.debug("Deleted trip id={}", id);
        } catch (RuntimeException ex) {
            log.warn("Trip not found for delete id={}: {}", id, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }
}
