package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.dto.TeamDTO;
import com.arjunakankipati.racingstatanalysis.model.Team;

import java.util.Map;
import java.util.Optional;

/**
 * Repository interface for Team entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface TeamRepository extends BaseRepository<Team, Long> {

    /**
     * Find a team by its name.
     *
     * @param name the name of the team to find
     * @return an Optional containing the found team, or empty if not found
     */
    Optional<Team> findByName(String name);

    /**
     * Find all teams with their cars and drivers for a specific event in a single query.
     * This method uses joins to fetch all related data efficiently and returns it in a structured format.
     *
     * @param eventId the ID of the event
     * @return a map of team IDs to TeamDTOs containing all team, car, and driver data
     */
    Map<Long, TeamDTO> findTeamsWithCarsAndDriversByEventId(Long eventId);
}
