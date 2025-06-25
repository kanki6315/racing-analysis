package com.arjunakankipati.racingstatanalysis.dto;

import java.util.Objects;

/**
 * Data Transfer Object for Class information.
 */
public class ClassDTO {
    private Long id;
    private Long seriesId;
    private String name;
    private String description;

    /**
     * Default constructor.
     */
    public ClassDTO() {
    }

    /**
     * Full constructor.
     *
     * @param id          the ID of the class
     * @param seriesId    the ID of the series this class belongs to
     * @param name        the name of the class
     * @param description the description of the class
     */
    public ClassDTO(Long id, Long seriesId, String name, String description) {
        this.id = id;
        this.seriesId = seriesId;
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the ID of the class.
     *
     * @return the ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the class.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the series this class belongs to.
     *
     * @return the series ID
     */
    public Long getSeriesId() {
        return seriesId;
    }

    /**
     * Sets the ID of the series this class belongs to.
     *
     * @param seriesId the series ID to set
     */
    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    /**
     * Gets the name of the class.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the class.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the class.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the class.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassDTO classDTO = (ClassDTO) o;
        return Objects.equals(id, classDTO.id) &&
                Objects.equals(seriesId, classDTO.seriesId) &&
                Objects.equals(name, classDTO.name) &&
                Objects.equals(description, classDTO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, seriesId, name, description);
    }

    @Override
    public String toString() {
        return "ClassDTO{" +
                "id=" + id +
                ", seriesId=" + seriesId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
} 