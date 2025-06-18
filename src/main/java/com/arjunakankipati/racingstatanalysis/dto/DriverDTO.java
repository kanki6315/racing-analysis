package com.arjunakankipati.racingstatanalysis.dto;

/**
 * DTO for Driver information.
 */
public class DriverDTO {
    private Long driverId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String nationality;
    private String hometown;
    private String licenseType;
    private Integer driverNumber;

    public DriverDTO() {
    }

    public DriverDTO(Long driverId, String firstName, String lastName, String nationality,
                     String hometown, String licenseType, Integer driverNumber) {
        this.driverId = driverId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.nationality = nationality;
        this.hometown = hometown;
        this.licenseType = licenseType;
        this.driverNumber = driverNumber;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateFullName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateFullName();
    }

    public String getFullName() {
        return fullName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public Integer getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(Integer driverNumber) {
        this.driverNumber = driverNumber;
    }

    private void updateFullName() {
        this.fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        this.fullName = this.fullName.trim();
    }
}