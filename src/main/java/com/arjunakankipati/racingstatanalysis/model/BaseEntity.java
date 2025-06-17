package com.arjunakankipati.racingstatanalysis.model;

/**
 * Base interface for all entity models.
 * Ensures that all entities have an ID field and methods to get/set it.
 *
 * @param <ID> the type of the entity's ID
 */
public interface BaseEntity<ID> {
    
    /**
     * Gets the ID of the entity.
     *
     * @return the ID
     */
    ID getId();
    
    /**
     * Sets the ID of the entity.
     *
     * @param id the ID to set
     */
    void setId(ID id);
}