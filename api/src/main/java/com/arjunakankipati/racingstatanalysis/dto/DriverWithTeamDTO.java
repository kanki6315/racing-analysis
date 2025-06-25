package com.arjunakankipati.racingstatanalysis.dto;

/**
 * DTO for Driver information including team and car details.
 */
public class DriverWithTeamDTO extends DriverDTO {
    private Long teamId;
    private String teamName;
    private String carNumber;
    private CarModelDTO carModel;

    public DriverWithTeamDTO() {
        super();
    }

    public DriverWithTeamDTO(Long driverId, String firstName, String lastName, String nationality,
                             String hometown, String licenseType, Integer driverNumber,
                             Long teamId, String teamName, String carNumber, CarModelDTO carModel) {
        super(driverId, firstName, lastName, nationality, hometown, licenseType, driverNumber);
        this.teamId = teamId;
        this.teamName = teamName;
        this.carNumber = carNumber;
        this.carModel = carModel;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public CarModelDTO getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModelDTO carModel) {
        this.carModel = carModel;
    }
} 