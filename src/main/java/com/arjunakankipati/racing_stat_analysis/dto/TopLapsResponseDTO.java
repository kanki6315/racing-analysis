package com.arjunakankipati.racing_stat_analysis.dto;

import java.util.List;

/**
 * Data Transfer Object for top laps responses.
 * Contains information about a driver and their top lap times.
 */
public class TopLapsResponseDTO {
    private DriverDTO driver;
    private List<LapTimeDTO> topLaps;

    /**
     * Default constructor.
     */
    public TopLapsResponseDTO() {
    }

    /**
     * Constructor with driver ID and top laps.
     *
     * @param driverId the ID of the driver
     * @param topLaps the list of top lap times
     */
    public TopLapsResponseDTO(Long driverId, List<LapTimeDTO> topLaps) {
        this.driver = new DriverDTO(driverId);
        this.topLaps = topLaps;
    }

    /**
     * Full constructor.
     *
     * @param driver the driver information
     * @param topLaps the list of top lap times
     */
    public TopLapsResponseDTO(DriverDTO driver, List<LapTimeDTO> topLaps) {
        this.driver = driver;
        this.topLaps = topLaps;
    }

    /**
     * Gets the driver information.
     *
     * @return the driver
     */
    public DriverDTO getDriver() {
        return driver;
    }

    /**
     * Sets the driver information.
     *
     * @param driver the driver to set
     */
    public void setDriver(DriverDTO driver) {
        this.driver = driver;
    }

    /**
     * Gets the list of top lap times.
     *
     * @return the top laps
     */
    public List<LapTimeDTO> getTopLaps() {
        return topLaps;
    }

    /**
     * Sets the list of top lap times.
     *
     * @param topLaps the top laps to set
     */
    public void setTopLaps(List<LapTimeDTO> topLaps) {
        this.topLaps = topLaps;
    }

    /**
     * Inner class to represent driver information.
     */
    public static class DriverDTO {
        private Long id;
        private String name;
        private String nationality;

        /**
         * Constructor with driver ID.
         *
         * @param id the ID of the driver
         */
        public DriverDTO(Long id) {
            this.id = id;
        }

        /**
         * Full constructor.
         *
         * @param id the ID of the driver
         * @param name the name of the driver
         * @param nationality the nationality of the driver
         */
        public DriverDTO(Long id, String name, String nationality) {
            this.id = id;
            this.name = name;
            this.nationality = nationality;
        }

        /**
         * Gets the ID of the driver.
         *
         * @return the ID
         */
        public Long getId() {
            return id;
        }

        /**
         * Sets the ID of the driver.
         *
         * @param id the ID to set
         */
        public void setId(Long id) {
            this.id = id;
        }

        /**
         * Gets the name of the driver.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name of the driver.
         *
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the nationality of the driver.
         *
         * @return the nationality
         */
        public String getNationality() {
            return nationality;
        }

        /**
         * Sets the nationality of the driver.
         *
         * @param nationality the nationality to set
         */
        public void setNationality(String nationality) {
            this.nationality = nationality;
        }
    }
}