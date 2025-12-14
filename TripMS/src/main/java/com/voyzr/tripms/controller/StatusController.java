package com.voyzr.tripms.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class StatusController {

    @Value("${spring.application.name}")
    private String appName;


    @GetMapping
    public String status() {
        return appName + " is UP!";
    }
}
