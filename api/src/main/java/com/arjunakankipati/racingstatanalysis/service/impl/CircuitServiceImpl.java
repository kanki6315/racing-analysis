package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.CircuitDTO;
import com.arjunakankipati.racingstatanalysis.exceptions.ResourceNotFoundException;
import com.arjunakankipati.racingstatanalysis.model.Circuit;
import com.arjunakankipati.racingstatanalysis.repository.CircuitRepository;
import com.arjunakankipati.racingstatanalysis.service.CircuitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CircuitServiceImpl implements CircuitService {
    private final CircuitRepository circuitRepository;

    @Autowired
    public CircuitServiceImpl(CircuitRepository circuitRepository) {
        this.circuitRepository = circuitRepository;
    }

    @Override
    public List<CircuitDTO> getAllCircuits() {
        return circuitRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CircuitDTO getCircuitById(Long id) {
        Circuit circuit = circuitRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        return toDTO(circuit);
    }

    @Override
    public CircuitDTO createCircuit(CircuitDTO circuitDTO) {
        Circuit circuit = toEntity(circuitDTO);
        circuit.setId(null); // Ensure ID is not set for new entity
        Circuit saved = circuitRepository.save(circuit);
        return toDTO(saved);
    }

    @Override
    public CircuitDTO updateCircuit(Long id, CircuitDTO circuitDTO) {
        Circuit existing = circuitRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        existing.setName(circuitDTO.getName());
        existing.setLengthMeters(circuitDTO.getLengthMeters());
        existing.setCountry(circuitDTO.getCountry());
        existing.setLocation(circuitDTO.getLocation());
        existing.setDescription(circuitDTO.getDescription());
        circuitRepository.save(existing);
        return toDTO(existing);
    }

    private CircuitDTO toDTO(Circuit circuit) {
        return new CircuitDTO(
                circuit.getId(),
                circuit.getName(),
                circuit.getLengthMeters(),
                circuit.getCountry(),
                circuit.getLocation(),
                circuit.getDescription()
        );
    }

    private Circuit toEntity(CircuitDTO dto) {
        return new Circuit(
                dto.getId(),
                dto.getName(),
                dto.getLengthMeters(),
                dto.getCountry(),
                dto.getLocation(),
                dto.getDescription()
        );
    }
} 