package com.arjunakankipati.racingstatanalysis.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Value("${api.key}")
    private String expectedApiKey;

    @GetMapping("/check")
    public ResponseEntity<Void> checkApiKey(@RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        if (apiKey != null && apiKey.equals(expectedApiKey)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
} 