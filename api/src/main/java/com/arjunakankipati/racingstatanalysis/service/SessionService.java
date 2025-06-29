package com.arjunakankipati.racingstatanalysis.service;

import com.arjunakankipati.racingstatanalysis.dto.SessionDTO;
import com.arjunakankipati.racingstatanalysis.dto.SessionsResponseDTO;

public interface SessionService {
    SessionDTO createSession(SessionDTO sessionDTO);

    /**
     * Finds all sessions for a specific event.
     *
     * @param eventId the ID of the event
     * @return a response containing the sessions for the event
     * @throws com.arjunakankipati.racingstatanalysis.exceptions.ResourceNotFoundException if the event is not found
     */
    SessionsResponseDTO findSessionsByEventId(Long eventId);
} 