package com.arjunakankipati.racing_stat_analysis.service.impl;

import com.arjunakankipati.racing_stat_analysis.dto.LapTimeDTO;
import com.arjunakankipati.racing_stat_analysis.service.DriverAnalysisService;
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