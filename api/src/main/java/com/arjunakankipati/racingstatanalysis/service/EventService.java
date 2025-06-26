package com.arjunakankipati.racingstatanalysis.service;

import com.arjunakankipati.racingstatanalysis.dto.EventDTO;

public interface EventService {
    /**
     * Creates a new event.
     *
     * @param eventDTO the event data
     * @return the created event as a response DTO
     */
    EventDTO createEvent(EventDTO eventDTO);
} 