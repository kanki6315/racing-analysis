package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.LapTimeDTO;
import com.arjunakankipati.racingstatanalysis.service.DriverAnalysisService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the DriverAnalysisService interface.
 */
@Service
public class DriverAnalysisServiceImpl implements DriverAnalysisService {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LapTimeDTO> getTopLapTimes(Long driverId, int percentage, 
                                         Optional<Long> seriesId, 
                                         Optional<Integer> year, 
                                         Optional<Long> eventId,
                                         Optional<Long> sessionId) {
        throw new UnsupportedOperationException("Method not implemented yet");
    }
}