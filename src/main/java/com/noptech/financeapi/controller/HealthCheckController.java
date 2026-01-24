package com.noptech.financeapi.controller;

import com.noptech.financeapi.dto.HeathCheckDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping(value = "/health", produces = "application/json")
    public HeathCheckDto healthCheck() {
        return new HeathCheckDto("ok");
    }
}
