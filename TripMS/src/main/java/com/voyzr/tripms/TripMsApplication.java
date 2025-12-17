package com.voyzr.tripms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class TripMsApplication {

    public static void main(String[] args) {
        log.info("Starting TripMS application");
        SpringApplication.run(TripMsApplication.class, args);
        log.info("TripMS application started successfully");
    }

}
