package com.voyzr.tripms.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StatusControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        StatusController controller = new StatusController();
        // inject appName via reflection since field is package-private with @Value
        try {
            java.lang.reflect.Field f = StatusController.class.getDeclaredField("appName");
            f.setAccessible(true);
            f.set(controller, "TripMS");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void root_returnsUpMessage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("TripMS is UP!"));
    }
}
