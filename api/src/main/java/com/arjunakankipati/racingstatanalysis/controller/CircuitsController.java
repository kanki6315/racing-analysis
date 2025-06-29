package com.arjunakankipati.racingstatanalysis.controller;

import com.arjunakankipati.racingstatanalysis.dto.CircuitDTO;
import com.arjunakankipati.racingstatanalysis.service.CircuitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/circuits")
public class CircuitsController {
    private final CircuitService circuitService;

    @Value("${api.key}")
    private String expectedApiKey;

    @Autowired
    public CircuitsController(CircuitService circuitService) {
        this.circuitService = circuitService;
    }

    @GetMapping
    public ResponseEntity<List<CircuitDTO>> getAllCircuits() {
        return ResponseEntity.ok(circuitService.getAllCircuits());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CircuitDTO> getCircuitById(@PathVariable Long id) {
        return ResponseEntity.ok(circuitService.getCircuitById(id));
    }

    @PostMapping
    public ResponseEntity<CircuitDTO> createCircuit(
            @RequestBody CircuitDTO circuitDTO,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CircuitDTO created = circuitService.createCircuit(circuitDTO);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CircuitDTO> updateCircuit(
            @PathVariable Long id,
            @RequestBody CircuitDTO circuitDTO,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CircuitDTO updated = circuitService.updateCircuit(id, circuitDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<CircuitDTO>> findAllCircuits(@RequestParam(value = "name", required = false) String name) {
        if (name != null && !name.isEmpty()) {
            return ResponseEntity.ok(circuitService.getAllCircuitsByName(name));
        } else {
            return ResponseEntity.ok(circuitService.getAllCircuits());
        }
    }
} 