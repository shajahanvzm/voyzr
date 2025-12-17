package com.voyzr.tripms.service.impl;

import com.voyzr.tripms.dto.TripDTO;
import com.voyzr.tripms.entity.Trip;
import com.voyzr.tripms.repository.TripRepository;
import com.voyzr.tripms.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    @Override
    public TripDTO createTrip(TripDTO tripDTO) {
        log.info("Creating trip entity from DTO: name='{}'", tripDTO != null ? tripDTO.getName() : null);
        Trip trip = Trip.builder()
                .name(tripDTO.getName())
                .description(tripDTO.getDescription())
                .startDate(tripDTO.getStartDate())
                .status(tripDTO.getStatus())
                .build();
        Trip savedTrip = tripRepository.save(trip);
        log.debug("Saved trip with id={}", savedTrip.getId());
        return mapToDTO(savedTrip);
    }

    @Override
    public TripDTO getTrip(Long id) {
        log.info("Fetching trip by id={}", id);
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Trip not found for id={}", id);
                    return new RuntimeException("Trip not found");
                });
        return mapToDTO(trip);
    }

    @Override
    public List<TripDTO> getAllTrips() {
        log.info("Fetching all trips");
        List<TripDTO> list = tripRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        log.debug("Fetched {} trips", list.size());
        return list;
    }

    @Override
    public TripDTO updateTrip(Long id, TripDTO tripDTO) {
        log.info("Updating trip id={}", id);
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Trip not found for update id={}", id);
                    return new RuntimeException("Trip not found");
                });

        existingTrip.setName(tripDTO.getName());
        existingTrip.setDescription(tripDTO.getDescription());
        existingTrip.setStartDate(tripDTO.getStartDate());
        existingTrip.setStatus(tripDTO.getStatus());

        Trip updatedTrip = tripRepository.save(existingTrip);
        log.debug("Updated trip persisted id={}", updatedTrip.getId());
        return mapToDTO(updatedTrip);
    }

    @Override
    public void deleteTrip(Long id) {
        log.info("Deleting trip id={}", id);
        if (!tripRepository.existsById(id)) {
            log.warn("Trip not found for delete id={}", id);
            throw new RuntimeException("Trip not found");
        }
        tripRepository.deleteById(id);
        log.debug("Trip deleted id={}", id);
    }

    private TripDTO mapToDTO(Trip trip) {
        log.trace("Mapping Trip entity to DTO id={}", trip != null ? trip.getId() : null);
        return TripDTO.builder()
                .id(trip.getId())
                .name(trip.getName())
                .description(trip.getDescription())
                .startDate(trip.getStartDate())
                .status(trip.getStatus())
                .build();
    }
}
