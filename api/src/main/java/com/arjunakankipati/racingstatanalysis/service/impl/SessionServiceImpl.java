package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.SessionDTO;
import com.arjunakankipati.racingstatanalysis.dto.SessionsResponseDTO;
import com.arjunakankipati.racingstatanalysis.exceptions.ResourceNotFoundException;
import com.arjunakankipati.racingstatanalysis.model.Event;
import com.arjunakankipati.racingstatanalysis.model.Session;
import com.arjunakankipati.racingstatanalysis.repository.EventRepository;
import com.arjunakankipati.racingstatanalysis.repository.SessionRepository;
import com.arjunakankipati.racingstatanalysis.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionServiceImpl implements SessionService {
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private EventRepository eventRepository;

    @Override
    public SessionDTO createSession(SessionDTO sessionDTO) {
        Session session = new Session();
        session.setEventId(sessionDTO.getEventId());
        session.setCircuitId(sessionDTO.getCircuitId());
        session.setName(sessionDTO.getName());
        session.setType(sessionDTO.getType());
        session.setStartDatetime(sessionDTO.getStartDatetime());
        session.setDurationSeconds(sessionDTO.getDurationSeconds());
        Session saved = sessionRepository.save(session);
        return new SessionDTO(
                saved.getId(),
                saved.getEventId(),
                saved.getCircuitId(),
                saved.getName(),
                saved.getType(),
                saved.getStartDatetime(),
                saved.getDurationSeconds()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SessionsResponseDTO findSessionsByEventId(Long eventId) {
        // Find the event by ID
        Event event = eventRepository.findById(eventId)
                .orElseThrow(ResourceNotFoundException::new);

        // Find all sessions for this event
        List<Session> sessions = sessionRepository.findByEventId(eventId);

        // Convert to DTOs
        List<SessionDTO> sessionDTOs = sessions.stream()
                .map(session -> new SessionDTO(
                        session.getId(),
                        session.getEventId(),
                        session.getCircuitId(),
                        session.getName(),
                        session.getType(),
                        session.getStartDatetime(),
                        session.getDurationSeconds()
                ))
                .toList();

        // Create response DTO
        return new SessionsResponseDTO(event.getId(), event.getName(), sessionDTOs);
    }
} 