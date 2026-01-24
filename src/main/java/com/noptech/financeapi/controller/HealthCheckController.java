package com.noptech.financeapi.controller;

import com.noptech.financeapi.dto.HeathCheckDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping(value = "/health", produces = "application/json")
    public ResponseEntity<HeathCheckDto> healthCheck() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(HeathCheckDto.builder().
                        status("OK")
                        .build());
    }
}
