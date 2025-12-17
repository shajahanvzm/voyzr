package com.voyzr.tripms.service;

import com.voyzr.tripms.dto.TripDTO;
import com.voyzr.tripms.entity.Trip;
import com.voyzr.tripms.entity.TripStatus;
import com.voyzr.tripms.repository.TripRepository;
import com.voyzr.tripms.service.impl.TripServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceImplTest {

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripServiceImpl tripService;

    private TripDTO sampleDTO;
    private Trip sampleEntity;

    @BeforeEach
    void setUp() {
        sampleDTO = TripDTO.builder()
                .name("Test Trip")
                .description("Desc")
                .startDate(LocalDate.of(2025, 1, 1))
                .status(TripStatus.Planned)
                .build();

        sampleEntity = Trip.builder()
                .id(1L)
                .name(sampleDTO.getName())
                .description(sampleDTO.getDescription())
                .startDate(sampleDTO.getStartDate())
                .status(sampleDTO.getStatus())
                .build();
    }

    @Test
    void createTrip_mapsAndSaves() {
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> {
            Trip t = invocation.getArgument(0);
            return Trip.builder()
                    .id(42L)
                    .name(t.getName())
                    .description(t.getDescription())
                    .startDate(t.getStartDate())
                    .status(t.getStatus())
                    .build();
        });

        TripDTO result = tripService.createTrip(sampleDTO);

        assertNotNull(result);
        assertEquals(42L, result.getId());
        assertEquals(sampleDTO.getName(), result.getName());

        ArgumentCaptor<Trip> captor = ArgumentCaptor.forClass(Trip.class);
        verify(tripRepository).save(captor.capture());
        assertEquals(sampleDTO.getStatus(), captor.getValue().getStatus());
    }

    @Test
    void getTrip_returnsDTO_whenFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));

        TripDTO dto = tripService.getTrip(1L);
        assertEquals(1L, dto.getId());
        assertEquals("Test Trip", dto.getName());
    }

    @Test
    void getTrip_throws_whenNotFound() {
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tripService.getTrip(99L));
        assertTrue(ex.getMessage().contains("Trip not found"));
    }

    @Test
    void getAllTrips_mapsList() {
        when(tripRepository.findAll()).thenReturn(List.of(sampleEntity));
        List<TripDTO> list = tripService.getAllTrips();
        assertEquals(1, list.size());
        assertEquals(sampleEntity.getName(), list.get(0).getName());
    }

    @Test
    void updateTrip_updates_whenFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TripDTO update = TripDTO.builder()
                .name("Updated")
                .description("New Desc")
                .startDate(LocalDate.of(2026, 2, 2))
                .status(TripStatus.Progress)
                .build();

        TripDTO result = tripService.updateTrip(1L, update);
        assertEquals("Updated", result.getName());
        assertEquals(TripStatus.Progress, result.getStatus());
    }

    @Test
    void updateTrip_throws_whenNotFound() {
        when(tripRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tripService.updateTrip(999L, sampleDTO));
        assertTrue(ex.getMessage().contains("Trip not found"));
    }

    @Test
    void deleteTrip_deletes_whenExists() {
        when(tripRepository.existsById(1L)).thenReturn(true);
        tripService.deleteTrip(1L);
        verify(tripRepository).deleteById(1L);
    }

    @Test
    void deleteTrip_throws_whenNotExists() {
        when(tripRepository.existsById(55L)).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> tripService.deleteTrip(55L));
        assertTrue(ex.getMessage().contains("Trip not found"));
        verify(tripRepository, never()).deleteById(anyLong());
    }
}
