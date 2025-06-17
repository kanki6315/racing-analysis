package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.Team;

import java.util.List;
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
     * Find teams by name containing the given string.
     *
     * @param nameContains the string to search for in team names
     * @return a list of teams with names containing the given string
     */
    List<Team> findByNameContaining(String nameContains);
}