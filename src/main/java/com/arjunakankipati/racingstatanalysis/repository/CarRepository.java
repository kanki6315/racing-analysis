package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.Car;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Car entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface CarRepository extends BaseRepository<Car, Long> {

    /**
     * Find cars by session ID.
     *
     * @param sessionId the ID of the session to find cars for
     * @return a list of cars for the given session
     */
    List<Car> findBySessionId(Long sessionId);

    /**
     * Find cars by team ID.
     *
     * @param teamId the ID of the team to find cars for
     * @return a list of cars for the given team
     */
    List<Car> findByTeamId(Long teamId);

    /**
     * Find cars by class ID.
     *
     * @param classId the ID of the class to find cars for
     * @return a list of cars for the given class
     */
    List<Car> findByClassId(Long classId);

    /**
     * Find cars by manufacturer ID.
     *
     * @param manufacturerId the ID of the manufacturer to find cars for
     * @return a list of cars for the given manufacturer
     */
    List<Car> findByManufacturerId(Long manufacturerId);

    /**
     * Find a car by session ID and car number.
     *
     * @param sessionId the ID of the session
     * @param number the car number
     * @return an Optional containing the found car, or empty if not found
     */
    Optional<Car> findBySessionIdAndNumber(Long sessionId, String number);

    /**
     * Find cars by tire supplier.
     *
     * @param tireSupplier the tire supplier to find cars for
     * @return a list of cars with the given tire supplier
     */
    List<Car> findByTireSupplier(String tireSupplier);

    /**
     * Find cars by event ID and class ID.
     * This is done by finding all sessions for the event, then all cars in those sessions
     * that belong to the specified class.
     *
     * @param eventId the ID of the event to find cars for
     * @param classId the ID of the class to find cars for
     * @return a list of cars in the specified class for the event
     */
    List<Car> findByEventIdAndClassId(Long eventId, Long classId);
}