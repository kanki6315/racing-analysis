package com.arjunakankipati.racingstatanalysis.service.impl;

import com.arjunakankipati.racingstatanalysis.dto.EventDTO;
import com.arjunakankipati.racingstatanalysis.model.Event;
import com.arjunakankipati.racingstatanalysis.repository.EventRepository;
import com.arjunakankipati.racingstatanalysis.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public EventDTO createEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setSeriesId(eventDTO.getSeriesId());
        event.setName(eventDTO.getName());
        event.setYear(eventDTO.getYear());
        event.setStartDate(eventDTO.getStartDate());
        event.setEndDate(eventDTO.getEndDate());
        event.setDescription(eventDTO.getDescription());
        Event saved = eventRepository.save(event);
        return new EventDTO(
                saved.getId(),
                saved.getSeriesId(),
                saved.getName(),
                saved.getYear(),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.getDescription()
        );
    }
} 