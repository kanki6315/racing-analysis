package com.arjunakankipati.racingstatanalysis.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a sector of a lap.
 * A lap is divided into multiple sectors for more detailed timing analysis.
 */
public class Sector implements BaseEntity<Long> {
    private Long id;
    private Long lapId;
    private Integer sectorNumber;
    private BigDecimal sectorTimeSeconds;
    private Boolean isPersonalBest;
    private Boolean isSessionBest;
    private Boolean isValid;
    private String invalidationReason;

    /**
     * Default constructor.
     */
    public Sector() {
    }

    /**
     * Full constructor.
     *
     * @param id the ID of the sector
     * @param lapId the ID of the lap this sector belongs to
     * @param sectorNumber the number of the sector
     * @param sectorTimeSeconds the time taken to complete the sector in seconds
     * @param isPersonalBest whether the sector time is the personal best for the driver
     * @param isSessionBest whether the sector time is the best in the session
     * @param isValid whether the sector time is valid
     * @param invalidationReason the reason for invalidation if the sector time is invalid
     */
    public Sector(Long id, Long lapId, Integer sectorNumber, BigDecimal sectorTimeSeconds,
                  Boolean isPersonalBest, Boolean isSessionBest, Boolean isValid, String invalidationReason) {
        this.id = id;
        this.lapId = lapId;
        this.sectorNumber = sectorNumber;
        this.sectorTimeSeconds = sectorTimeSeconds;
        this.isPersonalBest = isPersonalBest;
        this.isSessionBest = isSessionBest;
        this.isValid = isValid;
        this.invalidationReason = invalidationReason;
    }

    /**
     * Gets the ID of the sector.
     *
     * @return the ID
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the sector.
     *
     * @param id the ID to set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the lap this sector belongs to.
     *
     * @return the lap ID
     */
    public Long getLapId() {
        return lapId;
    }

    /**
     * Sets the ID of the lap this sector belongs to.
     *
     * @param lapId the lap ID to set
     */
    public void setLapId(Long lapId) {
        this.lapId = lapId;
    }

    /**
     * Gets the number of the sector.
     *
     * @return the sector number
     */
    public Integer getSectorNumber() {
        return sectorNumber;
    }

    /**
     * Sets the number of the sector.
     *
     * @param sectorNumber the sector number to set
     */
    public void setSectorNumber(Integer sectorNumber) {
        this.sectorNumber = sectorNumber;
    }

    /**
     * Gets the time taken to complete the sector in seconds.
     *
     * @return the sector time in seconds
     */
    public BigDecimal getSectorTimeSeconds() {
        return sectorTimeSeconds;
    }

    /**
     * Sets the time taken to complete the sector in seconds.
     *
     * @param sectorTimeSeconds the sector time in seconds to set
     */
    public void setSectorTimeSeconds(BigDecimal sectorTimeSeconds) {
        this.sectorTimeSeconds = sectorTimeSeconds;
    }

    /**
     * Gets whether the sector time is the personal best for the driver.
     *
     * @return true if the sector time is the personal best, false otherwise
     */
    public Boolean getIsPersonalBest() {
        return isPersonalBest;
    }

    /**
     * Sets whether the sector time is the personal best for the driver.
     *
     * @param isPersonalBest true if the sector time is the personal best, false otherwise
     */
    public void setIsPersonalBest(Boolean isPersonalBest) {
        this.isPersonalBest = isPersonalBest;
    }

    /**
     * Gets whether the sector time is the best in the session.
     *
     * @return true if the sector time is the session best, false otherwise
     */
    public Boolean getIsSessionBest() {
        return isSessionBest;
    }

    /**
     * Sets whether the sector time is the best in the session.
     *
     * @param isSessionBest true if the sector time is the session best, false otherwise
     */
    public void setIsSessionBest(Boolean isSessionBest) {
        this.isSessionBest = isSessionBest;
    }

    /**
     * Gets whether the sector time is valid.
     *
     * @return true if the sector time is valid, false otherwise
     */
    public Boolean getIsValid() {
        return isValid;
    }

    /**
     * Sets whether the sector time is valid.
     *
     * @param isValid true if the sector time is valid, false otherwise
     */
    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * Gets the reason for invalidation if the sector time is invalid.
     *
     * @return the invalidation reason
     */
    public String getInvalidationReason() {
        return invalidationReason;
    }

    /**
     * Sets the reason for invalidation if the sector time is invalid.
     *
     * @param invalidationReason the invalidation reason to set
     */
    public void setInvalidationReason(String invalidationReason) {
        this.invalidationReason = invalidationReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sector sector = (Sector) o;
        return Objects.equals(id, sector.id) &&
                Objects.equals(lapId, sector.lapId) &&
                Objects.equals(sectorNumber, sector.sectorNumber) &&
                Objects.equals(sectorTimeSeconds, sector.sectorTimeSeconds) &&
                Objects.equals(isPersonalBest, sector.isPersonalBest) &&
                Objects.equals(isSessionBest, sector.isSessionBest) &&
                Objects.equals(isValid, sector.isValid) &&
                Objects.equals(invalidationReason, sector.invalidationReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lapId, sectorNumber, sectorTimeSeconds, isPersonalBest, isSessionBest,
                isValid, invalidationReason);
    }

    @Override
    public String toString() {
        return "Sector{" +
                "id=" + id +
                ", lapId=" + lapId +
                ", sectorNumber=" + sectorNumber +
                ", sectorTimeSeconds=" + sectorTimeSeconds +
                ", isPersonalBest=" + isPersonalBest +
                ", isSessionBest=" + isSessionBest +
                ", isValid=" + isValid +
                ", invalidationReason='" + invalidationReason + '\'' +
                '}';
    }
}
