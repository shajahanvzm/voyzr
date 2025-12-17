package com.voyzr.tripms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voyzr.tripms.dto.TripDTO;
import com.voyzr.tripms.entity.TripStatus;
import com.voyzr.tripms.exception.GlobalExceptionHandler;
import com.voyzr.tripms.service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TripControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TripService tripService;

    @BeforeEach
    void setup() {
        tripService = Mockito.mock(TripService.class);
        TripController controller = new TripController(tripService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        // Support Java 8 date/time (LocalDate) serialization
        objectMapper.findAndRegisterModules();
    }

    private TripDTO sample(Long id) {
        return TripDTO.builder()
                .id(id)
                .name("Trip" + id)
                .description("D")
                .startDate(LocalDate.of(2025, 1, 1))
                .status(TripStatus.Planned)
                .build();
    }

    @Test
    void create_returns201() throws Exception {
        TripDTO request = sample(null);
        TripDTO response = sample(10L);
        given(tripService.createTrip(any(TripDTO.class))).willReturn(response);

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.status", is("Planned")));
    }

    @Test
    void getById_returns200() throws Exception {
        given(tripService.getTrip(5L)).willReturn(sample(5L));

        mockMvc.perform(get("/api/v1/trips/{id}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Trip5")));
    }

    @Test
    void getById_returns404_whenServiceThrows() throws Exception {
        given(tripService.getTrip(404L)).willThrow(new RuntimeException("Trip not found"));

        mockMvc.perform(get("/api/v1/trips/{id}", 404))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Trip not found")));
    }

    @Test
    void getAll_returnsList() throws Exception {
        given(tripService.getAllTrips()).willReturn(List.of(sample(1L), sample(2L)));

        mockMvc.perform(get("/api/v1/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void update_returns200() throws Exception {
        TripDTO req = sample(null);
        TripDTO resp = sample(7L);
        given(tripService.updateTrip(eq(7L), any(TripDTO.class))).willReturn(resp);

        mockMvc.perform(put("/api/v1/trips/{id}", 7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(7)));
    }

    @Test
    void update_returns404_whenServiceThrows() throws Exception {
        TripDTO req = sample(null);
        given(tripService.updateTrip(eq(99L), any(TripDTO.class))).willThrow(new RuntimeException("Trip not found"));

        mockMvc.perform(put("/api/v1/trips/{id}", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/trips/{id}", 3))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_returns404_whenServiceThrows() throws Exception {
        doThrow(new RuntimeException("Trip not found")).when(tripService).deleteTrip(77L);

        mockMvc.perform(delete("/api/v1/trips/{id}", 77))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void create_returns400_onInvalidEnum() throws Exception {
        String invalidJson = "{\n" +
                "  \"name\": \"T\",\n" +
                "  \"description\": \"D\",\n" +
                "  \"startDate\": \"2025-01-01\",\n" +
                "  \"status\": \"Invalid\"\n" +
                "}";

        mockMvc.perform(post("/api/v1/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", containsString("Malformed JSON request")));
    }
}
