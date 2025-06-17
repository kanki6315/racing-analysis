package com.arjunakankipati.racingstatanalysis.repository;

import com.arjunakankipati.racingstatanalysis.model.Sector;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Sector entity operations.
 * Extends BaseRepository to inherit common CRUD operations.
 */
public interface SectorRepository extends BaseRepository<Sector, Long> {

    /**
     * Find sectors by lap ID.
     *
     * @param lapId the ID of the lap to find sectors for
     * @return a list of sectors for the given lap
     */
    List<Sector> findByLapId(Long lapId);

    /**
     * Find a sector by lap ID and sector number.
     *
     * @param lapId the ID of the lap
     * @param sectorNumber the sector number
     * @return an Optional containing the found sector, or empty if not found
     */
    Optional<Sector> findByLapIdAndSectorNumber(Long lapId, Integer sectorNumber);

    /**
     * Find personal best sectors by lap ID.
     *
     * @param lapId the ID of the lap to find personal best sectors for
     * @return a list of personal best sectors for the given lap
     */
    List<Sector> findByLapIdAndIsPersonalBestTrue(Long lapId);

    /**
     * Find session best sectors by lap ID.
     *
     * @param lapId the ID of the lap to find session best sectors for
     * @return a list of session best sectors for the given lap
     */
    List<Sector> findByLapIdAndIsSessionBestTrue(Long lapId);

    /**
     * Find top N sectors by lap ID ordered by sector time.
     *
     * @param lapId the ID of the lap to find top sectors for
     * @param limit the number of top sectors to return
     * @return a list of top sectors for the given lap
     */
    List<Sector> findTopSectorsByLapId(Long lapId, int limit);
}