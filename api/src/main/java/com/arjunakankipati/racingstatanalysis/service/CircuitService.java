package com.arjunakankipati.racingstatanalysis.service;

import com.arjunakankipati.racingstatanalysis.dto.CircuitDTO;

import java.util.List;

public interface CircuitService {
    List<CircuitDTO> getAllCircuits();

    CircuitDTO getCircuitById(Long id);

    CircuitDTO createCircuit(CircuitDTO circuitDTO);

    CircuitDTO updateCircuit(Long id, CircuitDTO circuitDTO);

    List<CircuitDTO> getAllCircuitsByName(String namePart);
} 