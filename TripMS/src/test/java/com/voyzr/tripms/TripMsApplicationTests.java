package com.voyzr.tripms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.jpa.show-sql=false"
})
@ActiveProfiles("test")
class TripMsApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring context starts with the test profile
    }

}
