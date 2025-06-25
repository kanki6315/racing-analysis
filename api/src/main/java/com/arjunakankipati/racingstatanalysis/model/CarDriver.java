package com.arjunakankipati.racingstatanalysis.model;

import java.util.Objects;

/**
 * Represents the relationship between a car and a driver.
 * This is a junction entity for the many-to-many relationship between cars and drivers.
 */
public class CarDriver implements BaseEntity<Long> {
    private Long id;
    private Long carId;
    private Long driverId;
    private Integer driverNumber;

    /**
     * Default constructor.
     */
    public CarDriver() {
    }

    /**
     * Full constructor.
     *
     * @param id the ID of the car-driver relationship
     * @param carId the ID of the car
     * @param driverId the ID of the driver
     * @param driverNumber the number assigned to the driver within the car
     */
    public CarDriver(Long id, Long carId, Long driverId, Integer driverNumber) {
        this.id = id;
        this.carId = carId;
        this.driverId = driverId;
        this.driverNumber = driverNumber;
    }

    /**
     * Gets the ID of the car-driver relationship.
     *
     * @return the ID
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the car-driver relationship.
     *
     * @param id the ID to set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the car.
     *
     * @return the car ID
     */
    public Long getCarId() {
        return carId;
    }

    /**
     * Sets the ID of the car.
     *
     * @param carId the car ID to set
     */
    public void setCarId(Long carId) {
        this.carId = carId;
    }

    /**
     * Gets the ID of the driver.
     *
     * @return the driver ID
     */
    public Long getDriverId() {
        return driverId;
    }

    /**
     * Sets the ID of the driver.
     *
     * @param driverId the driver ID to set
     */
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    /**
     * Gets the number assigned to the driver within the car.
     *
     * @return the driver number
     */
    public Integer getDriverNumber() {
        return driverNumber;
    }

    /**
     * Sets the number assigned to the driver within the car.
     *
     * @param driverNumber the driver number to set
     */
    public void setDriverNumber(Integer driverNumber) {
        this.driverNumber = driverNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarDriver carDriver = (CarDriver) o;
        return Objects.equals(id, carDriver.id) &&
                Objects.equals(carId, carDriver.carId) &&
                Objects.equals(driverId, carDriver.driverId) &&
                Objects.equals(driverNumber, carDriver.driverNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, carId, driverId, driverNumber);
    }

    @Override
    public String toString() {
        return "CarDriver{" +
                "id=" + id +
                ", carId=" + carId +
                ", driverId=" + driverId +
                ", driverNumber=" + driverNumber +
                '}';
    }
}