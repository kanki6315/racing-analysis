package com.arjunakankipati.racing_stat_analysis.model;

import java.util.Objects;

/**
 * Represents a racing team (e.g., "Paul Miller Racing").
 * A team participates in racing events with one or more cars.
 */
public class Team implements BaseEntity<Long> {
    private Long id;
    private String name;
    private String description;

    /**
     * Default constructor.
     */
    public Team() {
    }

    /**
     * Full constructor.
     *
     * @param id the ID of the team
     * @param name the name of the team
     * @param description the description of the team
     */
    public Team(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the ID of the team.
     *
     * @return the ID
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the team.
     *
     * @param id the ID to set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the team.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the team.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the team.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the team.
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
        Team team = (Team) o;
        return Objects.equals(id, team.id) &&
                Objects.equals(name, team.name) &&
                Objects.equals(description, team.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}