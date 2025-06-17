package com.arjunakankipati.racing_stat_analysis.model;

import java.util.Objects;

/**
 * Represents a racing driver (e.g., "Connor De Phillippi").
 * A driver participates in racing events as part of a team.
 */
public class Driver implements BaseEntity<Long> {
    private Long id;
    private String firstName;
    private String lastName;
    private String nationality;
    private String hometown;
    private String licenseType;
    private String externalId;

    /**
     * Default constructor.
     */
    public Driver() {
    }

    /**
     * Full constructor.
     *
     * @param id the ID of the driver
     * @param firstName the first name of the driver
     * @param lastName the last name of the driver
     * @param nationality the nationality of the driver
     * @param hometown the hometown of the driver
     * @param licenseType the license type of the driver
     * @param externalId the external ID of the driver
     */
    public Driver(Long id, String firstName, String lastName, String nationality, String hometown, String licenseType, String externalId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationality = nationality;
        this.hometown = hometown;
        this.licenseType = licenseType;
        this.externalId = externalId;
    }

    /**
     * Gets the ID of the driver.
     *
     * @return the ID
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the driver.
     *
     * @param id the ID to set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the first name of the driver.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the driver.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the driver.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the driver.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the full name of the driver.
     *
     * @return the full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
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

    /**
     * Gets the hometown of the driver.
     *
     * @return the hometown
     */
    public String getHometown() {
        return hometown;
    }

    /**
     * Sets the hometown of the driver.
     *
     * @param hometown the hometown to set
     */
    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    /**
     * Gets the license type of the driver.
     *
     * @return the license type
     */
    public String getLicenseType() {
        return licenseType;
    }

    /**
     * Sets the license type of the driver.
     *
     * @param licenseType the license type to set
     */
    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    /**
     * Gets the external ID of the driver.
     *
     * @return the external ID
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Sets the external ID of the driver.
     *
     * @param externalId the external ID to set
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return Objects.equals(id, driver.id) &&
                Objects.equals(firstName, driver.firstName) &&
                Objects.equals(lastName, driver.lastName) &&
                Objects.equals(nationality, driver.nationality) &&
                Objects.equals(hometown, driver.hometown) &&
                Objects.equals(licenseType, driver.licenseType) &&
                Objects.equals(externalId, driver.externalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, nationality, hometown, licenseType, externalId);
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", nationality='" + nationality + '\'' +
                ", hometown='" + hometown + '\'' +
                ", licenseType='" + licenseType + '\'' +
                ", externalId='" + externalId + '\'' +
                '}';
    }
}