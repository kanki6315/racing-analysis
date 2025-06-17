package com.arjunakankipati.racing_stat_analysis.repository;

import com.arjunakankipati.racing_stat_analysis.model.BaseEntity;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface for CRUD operations on entities.
 *
 * @param <T> the entity type
 * @param <ID> the type of the entity's ID
 */
public interface BaseRepository<T extends BaseEntity<ID>, ID> {

    /**
     * Find all entities.
     *
     * @return a list of all entities
     */
    List<T> findAll();

    /**
     * Find an entity by its ID.
     *
     * @param id the ID of the entity to find
     * @return an Optional containing the found entity, or empty if not found
     */
    Optional<T> findById(ID id);

    /**
     * Save an entity.
     * If the entity has an ID, it will be updated; otherwise, it will be inserted.
     *
     * @param entity the entity to save
     * @return the saved entity with its ID populated if it was an insert
     */
    T save(T entity);

    /**
     * Delete an entity by its ID.
     *
     * @param id the ID of the entity to delete
     * @return true if the entity was deleted, false if it was not found
     */
    boolean deleteById(ID id);

    /**
     * Count the number of entities.
     *
     * @return the number of entities
     */
    long count();
}