package com.arjunakankipati.racingstatanalysis.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Response DTO for classes that competed in an event.
 */
public class ClassesResponseDTO {
    private Long eventId;
    private String eventName;
    private List<ClassDTO> classes;

    /**
     * Default constructor.
     */
    public ClassesResponseDTO() {
        this.classes = new ArrayList<>();
    }

    /**
     * Constructor with event information.
     *
     * @param eventId   the ID of the event
     * @param eventName the name of the event
     */
    public ClassesResponseDTO(Long eventId, String eventName) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.classes = new ArrayList<>();
    }

    /**
     * Constructor with event information and classes.
     *
     * @param eventId   the ID of the event
     * @param eventName the name of the event
     * @param classes   the list of classes
     */
    public ClassesResponseDTO(Long eventId, String eventName, List<ClassDTO> classes) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.classes = classes != null ? classes : new ArrayList<>();
    }

    /**
     * Gets the ID of the event.
     *
     * @return the event ID
     */
    public Long getEventId() {
        return eventId;
    }

    /**
     * Sets the ID of the event.
     *
     * @param eventId the event ID to set
     */
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the name of the event.
     *
     * @return the event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the name of the event.
     *
     * @param eventName the event name to set
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Gets the list of classes.
     *
     * @return the classes
     */
    public List<ClassDTO> getClasses() {
        return classes;
    }

    /**
     * Sets the list of classes.
     *
     * @param classes the classes to set
     */
    public void setClasses(List<ClassDTO> classes) {
        this.classes = classes != null ? classes : new ArrayList<>();
    }

    /**
     * Adds a class to the list.
     *
     * @param classDTO the class to add
     */
    public void addClass(ClassDTO classDTO) {
        if (this.classes == null) {
            this.classes = new ArrayList<>();
        }
        this.classes.add(classDTO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassesResponseDTO that = (ClassesResponseDTO) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(eventName, that.eventName) &&
                Objects.equals(classes, that.classes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventName, classes);
    }

    @Override
    public String toString() {
        return "ClassesResponseDTO{" +
                "eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", classes=" + classes +
                '}';
    }
} 