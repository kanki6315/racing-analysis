package com.arjunakankipati.racingstatanalysis.controller;

import com.arjunakankipati.racingstatanalysis.dto.SessionDTO;
import com.arjunakankipati.racingstatanalysis.dto.SessionsResponseDTO;
import com.arjunakankipati.racingstatanalysis.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SessionsController {
    private final SessionService sessionService;

    @Value("${api.key}")
    private String expectedApiKey;

    @Autowired
    public SessionsController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/api/v1/sessions")
    public ResponseEntity<SessionDTO> createSession(
            @RequestBody SessionDTO sessionDTO,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        SessionDTO response = sessionService.createSession(sessionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Gets all sessions for a specific event.
     *
     * @param eventId the ID of the event
     * @return a response entity containing the sessions for the event
     */
    @GetMapping("/api/v1/events/{eventId}/sessions")
    public ResponseEntity<SessionsResponseDTO> getSessionsByEventId(@PathVariable Long eventId) {
        SessionsResponseDTO sessions = sessionService.findSessionsByEventId(eventId);
        return ResponseEntity.ok(sessions);
    }
} 