package com.voyzr.tripms.service;

import com.voyzr.tripms.dto.TripDTO;

import java.util.List;

/**
 * TripService defines the contract for managing trips.
 */
public interface TripService {

    TripDTO createTrip(TripDTO tripDTO);

    TripDTO getTrip(Long id);

    List<TripDTO> getAllTrips();

    TripDTO updateTrip(Long id, TripDTO tripDTO);

    void deleteTrip(Long id);
}
