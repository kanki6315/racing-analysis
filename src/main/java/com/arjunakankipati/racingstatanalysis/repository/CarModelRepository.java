package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.CarModel;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CarModel entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface CarModelRepository extends BaseRepository<CarModel, Long> {

    /**
     * Find car models by manufacturer ID.
     *
     * @param manufacturerId the ID of the manufacturer to find car models for
     * @return a list of car models for the given manufacturer
     */
    List<CarModel> findByManufacturerId(Long manufacturerId);

    /**
     * Find car models by name containing the given string.
     *
     * @param nameContains the string to search for in car model names
     * @return a list of car models with names containing the given string
     */
    List<CarModel> findByNameContaining(String nameContains);

    /**
     * Find car models by full name containing the given string.
     *
     * @param fullNameContains the string to search for in car model full names
     * @return a list of car models with full names containing the given string
     */
    List<CarModel> findByFullNameContaining(String fullNameContains);

    /**
     * Find car models by year model.
     *
     * @param yearModel the year model to find car models for
     * @return a list of car models for the given year
     */
    List<CarModel> findByYearModel(Integer yearModel);

    /**
     * Find a car model by manufacturer ID and name.
     *
     * @param manufacturerId the ID of the manufacturer
     * @param name           the name of the car model
     * @return an Optional containing the found car model, or empty if not found
     */
    Optional<CarModel> findByManufacturerIdAndName(Long manufacturerId, String name);

    /**
     * Find car models by manufacturer ID and year model.
     *
     * @param manufacturerId the ID of the manufacturer
     * @param yearModel      the year model
     * @return a list of car models for the given manufacturer and year
     */
    List<CarModel> findByManufacturerIdAndYearModel(Long manufacturerId, Integer yearModel);
} 