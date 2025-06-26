package com.arjunakankipati.racingstatanalysis.controller;

import com.arjunakankipati.racingstatanalysis.dto.*;
import com.arjunakankipati.racingstatanalysis.model.*;
import com.arjunakankipati.racingstatanalysis.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for session results operations.
 */
@RestController
public class ResultsController {
    private final ResultRepository resultRepository;
    private final CarEntryRepository carEntryRepository;
    private final CarDriverRepository carDriverRepository;
    private final DriverRepository driverRepository;
    private final CarModelRepository carModelRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public ResultsController(ResultRepository resultRepository, CarEntryRepository carEntryRepository, CarDriverRepository carDriverRepository, DriverRepository driverRepository, CarModelRepository carModelRepository, TeamRepository teamRepository) {
        this.resultRepository = resultRepository;
        this.carEntryRepository = carEntryRepository;
        this.carDriverRepository = carDriverRepository;
        this.driverRepository = driverRepository;
        this.carModelRepository = carModelRepository;
        this.teamRepository = teamRepository;
    }

    @GetMapping("/api/v1/sessions/{sessionId}/results")
    public ResponseEntity<ResultsResponseDTO> getResultsBySessionId(@PathVariable Long sessionId) {
        List<Result> results = resultRepository.findBySessionId(sessionId);
        List<ResultDTO> resultDTOS = results.stream().map(this::toDTO).collect(Collectors.toList());
        ResultsResponseDTO response = new ResultsResponseDTO(sessionId, resultDTOS);
        return ResponseEntity.ok(response);
    }

    private ResultDTO toDTO(Result result) {
        CarEntryDTO carEntryDTO = null;
        if (result.getCarEntryId() != null) {
            CarEntry carEntry = carEntryRepository.findById(result.getCarEntryId()).orElse(null);
            if (carEntry != null) {
                CarModelDTO carModelDTO = null;
                if (carEntry.getCarModelId() != null) {
                    CarModel carModel = carModelRepository.findById(carEntry.getCarModelId()).orElse(null);
                    if (carModel != null) {
                        carModelDTO = new CarModelDTO(
                                carModel.getId(),
                                carModel.getName(),
                                carModel.getFullName(),
                                carModel.getYearModel(),
                                carModel.getDescription()
                        );
                    }
                }
                String teamName = null;
                if (carEntry.getTeamId() != null) {
                    Team team = teamRepository.findById(carEntry.getTeamId()).orElse(null);
                    if (team != null) {
                        teamName = team.getName();
                    }
                }
                carEntryDTO = new CarEntryDTO(
                        carEntry.getId(),
                        carEntry.getNumber(),
                        carModelDTO,
                        carEntry.getTireSupplier(),
                        carEntry.getClassId(),
                        carEntry.getTeamId(),
                        teamName
                );
                // Get drivers for this car entry
                List<CarDriver> carDrivers = carDriverRepository.findByCarId(carEntry.getId());
                var driverDTOs = carDrivers.stream().map(carDriver -> {
                    Driver driver = driverRepository.findById(carDriver.getDriverId()).orElse(null);
                    if (driver != null) {
                        return new DriverDTO(
                                driver.getId(),
                                driver.getFirstName(),
                                driver.getLastName(),
                                driver.getNationality(),
                                driver.getHometown(),
                                driver.getLicenseType(),
                                carDriver.getDriverNumber()
                        );
                    }
                    return null;
                }).filter(d -> d != null).collect(Collectors.toList());
                carEntryDTO.setDrivers(driverDTOs);
            }
        }
        return new ResultDTO(
                result.getId(),
                result.getSessionId(),
                result.getCarEntryId(),
                result.getCarNumber(),
                result.getTires(),
                result.getStatus(),
                result.getLaps(),
                result.getTotalTime(),
                result.getGapFirst(),
                result.getGapPrevious(),
                result.getFlLapnum(),
                result.getFlTime(),
                result.getFlKph(),
                result.getPosition(),
                carEntryDTO
        );
    }
} 