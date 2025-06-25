package com.arjunakankipati.racingstatanalysis.dto;

/**
 * Data Transfer Object for driver-specific lap time analysis data.
 * Contains driver information, car/team/class details, and lap time analysis in a flat structure.
 */
public class DriverLapTimeAnalysisDTO {
    // Driver information
    private Long driverId;
    private String driverName;
    private String nationality;

    // Car information
    private Long carId;
    private String carNumber;
    private String carModel;

    // Team information
    private Long teamId;
    private String teamName;

    // Class information
    private Long classId;
    private String className;

    // Lap time analysis
    private String averageLapTime;
    private String fastestLapTime;
    private String medianLapTime;
    private Integer totalLapCount;

    /**
     * Default constructor.
     */
    public DriverLapTimeAnalysisDTO() {
    }

    /**
     * Full constructor.
     *
     * @param driverId       the ID of the driver
     * @param driverName     the name of the driver
     * @param nationality    the nationality of the driver
     * @param carId          the ID of the car
     * @param carNumber      the car number
     * @param carModel       the car model
     * @param teamId         the ID of the team
     * @param teamName       the name of the team
     * @param classId        the ID of the class
     * @param className      the name of the class
     * @param averageLapTime the average lap time in format "m:ss.SSS"
     * @param fastestLapTime the fastest lap time in format "m:ss.SSS"
     * @param medianLapTime  the median lap time in format "m:ss.SSS"
     * @param totalLapCount  the total number of laps
     */
    public DriverLapTimeAnalysisDTO(Long driverId, String driverName, String nationality,
                                    Long carId, String carNumber, String carModel,
                                    Long teamId, String teamName,
                                    Long classId, String className,
                                    String averageLapTime, String fastestLapTime, String medianLapTime,
                                    Integer totalLapCount) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.nationality = nationality;
        this.carId = carId;
        this.carNumber = carNumber;
        this.carModel = carModel;
        this.teamId = teamId;
        this.teamName = teamName;
        this.classId = classId;
        this.className = className;
        this.averageLapTime = averageLapTime;
        this.fastestLapTime = fastestLapTime;
        this.medianLapTime = medianLapTime;
        this.totalLapCount = totalLapCount;
    }

    // Getters and setters

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
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

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAverageLapTime() {
        return averageLapTime;
    }

    public void setAverageLapTime(String averageLapTime) {
        this.averageLapTime = averageLapTime;
    }

    public String getFastestLapTime() {
        return fastestLapTime;
    }

    public void setFastestLapTime(String fastestLapTime) {
        this.fastestLapTime = fastestLapTime;
    }

    public String getMedianLapTime() {
        return medianLapTime;
    }

    public void setMedianLapTime(String medianLapTime) {
        this.medianLapTime = medianLapTime;
    }

    public Integer getTotalLapCount() {
        return totalLapCount;
    }

    public void setTotalLapCount(Integer totalLapCount) {
        this.totalLapCount = totalLapCount;
    }
}