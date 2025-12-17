package com.voyzr.tripms.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenAPIConfigTest {

    @Test
    void tripMicroserviceOpenAPI_createsBean() {
        OpenAPIConfig cfg = new OpenAPIConfig();
        OpenAPI api = cfg.tripMicroserviceOpenAPI();
        assertNotNull(api);
        assertNotNull(api.getInfo());
        assertEquals("Trip Microservice API", api.getInfo().getTitle());
    }
}
