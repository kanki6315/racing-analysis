package com.arjunakankipati.racingstatanalysis.dto;

import java.util.List;
import java.util.ArrayList;

/**
 * DTO for response containing a list of teams that participated in an event.
 */
public class TeamsResponseDTO {
    private Long eventId;
    private String eventName;
    private List<TeamDTO> teams;

    public TeamsResponseDTO() {
        this.teams = new ArrayList<>();
    }

    public TeamsResponseDTO(Long eventId, String eventName) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.teams = new ArrayList<>();
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public List<TeamDTO> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamDTO> teams) {
        this.teams = teams;
    }

    public void addTeam(TeamDTO team) {
        if (this.teams == null) {
            this.teams = new ArrayList<>();
        }
        this.teams.add(team);
    }
}