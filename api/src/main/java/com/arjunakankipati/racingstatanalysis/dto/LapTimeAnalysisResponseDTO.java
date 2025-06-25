package com.arjunakankipati.racingstatanalysis.dto;

import java.util.List;
import java.util.ArrayList;

/**
 * Data Transfer Object for lap time analysis responses.
 * Contains information about an event and a list of driver-specific lap time analyses.
 */
public class LapTimeAnalysisResponseDTO {
    private EventDTO event;
    private List<DriverLapTimeAnalysisDTO> driverAnalyses;
    private LapTimeAnalysisDTO overallAnalysis;

    /**
     * Default constructor.
     */
    public LapTimeAnalysisResponseDTO() {
        this.driverAnalyses = new ArrayList<>();
    }

    /**
     * Constructor with event ID and driver analyses.
     *
     * @param eventId        the ID of the event
     * @param driverAnalyses the list of driver-specific lap time analyses
     */
    public LapTimeAnalysisResponseDTO(Long eventId, List<DriverLapTimeAnalysisDTO> driverAnalyses) {
        this.event = new EventDTO(eventId);
        this.driverAnalyses = driverAnalyses;
    }

    /**
     * Constructor with event ID, driver analyses, and overall analysis.
     *
     * @param eventId         the ID of the event
     * @param driverAnalyses  the list of driver-specific lap time analyses
     * @param overallAnalysis the overall lap time analysis for the event
     */
    public LapTimeAnalysisResponseDTO(Long eventId, List<DriverLapTimeAnalysisDTO> driverAnalyses, LapTimeAnalysisDTO overallAnalysis) {
        this.event = new EventDTO(eventId);
        this.driverAnalyses = driverAnalyses;
        this.overallAnalysis = overallAnalysis;
    }

    /**
     * Full constructor.
     *
     * @param event           the event information
     * @param driverAnalyses  the list of driver-specific lap time analyses
     * @param overallAnalysis the overall lap time analysis for the event
     */
    public LapTimeAnalysisResponseDTO(EventDTO event, List<DriverLapTimeAnalysisDTO> driverAnalyses, LapTimeAnalysisDTO overallAnalysis) {
        this.event = event;
        this.driverAnalyses = driverAnalyses;
        this.overallAnalysis = overallAnalysis;
    }

    /**
     * Gets the event information.
     *
     * @return the event
     */
    public EventDTO getEvent() {
        return event;
    }

    /**
     * Sets the event information.
     *
     * @param event the event to set
     */
    public void setEvent(EventDTO event) {
        this.event = event;
    }

    /**
     * Gets the list of driver-specific lap time analyses.
     *
     * @return the driver analyses
     */
    public List<DriverLapTimeAnalysisDTO> getDriverAnalyses() {
        return driverAnalyses;
    }

    /**
     * Sets the list of driver-specific lap time analyses.
     *
     * @param driverAnalyses the driver analyses to set
     */
    public void setDriverAnalyses(List<DriverLapTimeAnalysisDTO> driverAnalyses) {
        this.driverAnalyses = driverAnalyses;
    }

    /**
     * Gets the overall lap time analysis for the event.
     *
     * @return the overall analysis
     */
    public LapTimeAnalysisDTO getOverallAnalysis() {
        return overallAnalysis;
    }

    /**
     * Sets the overall lap time analysis for the event.
     *
     * @param overallAnalysis the overall analysis to set
     */
    public void setOverallAnalysis(LapTimeAnalysisDTO overallAnalysis) {
        this.overallAnalysis = overallAnalysis;
    }

    /**
     * Inner class to represent event information.
     */
    public static class EventDTO {
        private Long id;
        private String name;
        private Integer year;

        /**
         * Constructor with event ID.
         *
         * @param id the ID of the event
         */
        public EventDTO(Long id) {
            this.id = id;
        }

        /**
         * Full constructor.
         *
         * @param id   the ID of the event
         * @param name the name of the event
         * @param year the year of the event
         */
        public EventDTO(Long id, String name, Integer year) {
            this.id = id;
            this.name = name;
            this.year = year;
        }

        /**
         * Gets the ID of the event.
         *
         * @return the ID
         */
        public Long getId() {
            return id;
        }

        /**
         * Sets the ID of the event.
         *
         * @param id the ID to set
         */
        public void setId(Long id) {
            this.id = id;
        }

        /**
         * Gets the name of the event.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name of the event.
         *
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the year of the event.
         *
         * @return the year
         */
        public Integer getYear() {
            return year;
        }

        /**
         * Sets the year of the event.
         *
         * @param year the year to set
         */
        public void setYear(Integer year) {
            this.year = year;
        }
    }
}
