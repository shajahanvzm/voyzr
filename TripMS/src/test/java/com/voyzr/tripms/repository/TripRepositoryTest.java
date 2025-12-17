package com.voyzr.tripms.repository;

import com.voyzr.tripms.entity.Trip;
import com.voyzr.tripms.entity.TripStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.jpa.show-sql=false"
})
@ActiveProfiles("test")
class TripRepositoryTest {

    @Autowired
    private TripRepository tripRepository;

    private Trip newTrip(String name) {
        return Trip.builder()
                .name(name)
                .description("Repo test")
                .startDate(LocalDate.of(2025, 1, 1))
                .status(TripStatus.Planned)
                .build();
    }

    @Test
    void save_and_findById_and_update_and_delete_flow() {
        Trip saved = tripRepository.save(newTrip("RepoTrip"));
        assertNotNull(saved.getId());

        Optional<Trip> fetched = tripRepository.findById(saved.getId());
        assertTrue(fetched.isPresent());
        assertEquals("RepoTrip", fetched.get().getName());

        // update
        fetched.get().setStatus(TripStatus.Progress);
        Trip updated = tripRepository.save(fetched.get());
        assertEquals(TripStatus.Progress, updated.getStatus());

        // exists
        assertTrue(tripRepository.existsById(updated.getId()));

        // findAll
        List<Trip> all = tripRepository.findAll();
        assertFalse(all.isEmpty());

        // delete
        tripRepository.deleteById(updated.getId());
        assertFalse(tripRepository.findById(updated.getId()).isPresent());
    }
}
