package com.arjunakankipati.racingstatanalysis.dto;

import java.math.BigDecimal;

public class ResultDTO {
    private Long id;
    private Long sessionId;
    private Long carEntryId;
    private String carNumber;
    private String tires;
    private String status;
    private Integer laps;
    private String totalTime;
    private String gapFirst;
    private String gapPrevious;
    private Integer flLapnum;
    private String flTime;
    private BigDecimal flKph;
    private Integer position;
    private CarEntryDTO carEntry;

    public ResultDTO() {
    }

    public ResultDTO(Long id, Long sessionId, Long carEntryId, String carNumber, String tires, String status, Integer laps, String totalTime, String gapFirst, String gapPrevious, Integer flLapnum, String flTime, BigDecimal flKph, Integer position, CarEntryDTO carEntry) {
        this.id = id;
        this.sessionId = sessionId;
        this.carEntryId = carEntryId;
        this.carNumber = carNumber;
        this.tires = tires;
        this.status = status;
        this.laps = laps;
        this.totalTime = totalTime;
        this.gapFirst = gapFirst;
        this.gapPrevious = gapPrevious;
        this.flLapnum = flLapnum;
        this.flTime = flTime;
        this.flKph = flKph;
        this.position = position;
        this.carEntry = carEntry;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getCarEntryId() {
        return carEntryId;
    }

    public void setCarEntryId(Long carEntryId) {
        this.carEntryId = carEntryId;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getTires() {
        return tires;
    }

    public void setTires(String tires) {
        this.tires = tires;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getLaps() {
        return laps;
    }

    public void setLaps(Integer laps) {
        this.laps = laps;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getGapFirst() {
        return gapFirst;
    }

    public void setGapFirst(String gapFirst) {
        this.gapFirst = gapFirst;
    }

    public String getGapPrevious() {
        return gapPrevious;
    }

    public void setGapPrevious(String gapPrevious) {
        this.gapPrevious = gapPrevious;
    }

    public Integer getFlLapnum() {
        return flLapnum;
    }

    public void setFlLapnum(Integer flLapnum) {
        this.flLapnum = flLapnum;
    }

    public String getFlTime() {
        return flTime;
    }

    public void setFlTime(String flTime) {
        this.flTime = flTime;
    }

    public BigDecimal getFlKph() {
        return flKph;
    }

    public void setFlKph(BigDecimal flKph) {
        this.flKph = flKph;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public CarEntryDTO getCarEntry() {
        return carEntry;
    }

    public void setCarEntry(CarEntryDTO carEntry) {
        this.carEntry = carEntry;
    }
}