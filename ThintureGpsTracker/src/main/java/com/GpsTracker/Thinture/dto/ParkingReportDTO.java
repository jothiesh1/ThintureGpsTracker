package com.GpsTracker.Thinture.dto;

import java.sql.Timestamp;
public class ParkingReportDTO {
    private String deviceId;
    private String startParkedTime;
    private String parkedLatitude;
    private String parkedLongitude;
    private String endParkedTime;
    private long parkedDurationMinutes;
    private String parkedDurationFull;

    // Getters and Setters
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStartParkedTime() {
        return startParkedTime;
    }

    public void setStartParkedTime(String startParkedTime) {
        this.startParkedTime = startParkedTime;
    }

    public String getParkedLatitude() {
        return parkedLatitude;
    }

    public void setParkedLatitude(String parkedLatitude) {
        this.parkedLatitude = parkedLatitude;
    }

    public String getParkedLongitude() {
        return parkedLongitude;
    }

    public void setParkedLongitude(String parkedLongitude) {
        this.parkedLongitude = parkedLongitude;
    }

    public String getEndParkedTime() {
        return endParkedTime;
    }

    public void setEndParkedTime(String endParkedTime) {
        this.endParkedTime = endParkedTime;
    }

    public long getParkedDurationMinutes() {
        return parkedDurationMinutes;
    }

    public void setParkedDurationMinutes(long parkedDurationMinutes) {
        this.parkedDurationMinutes = parkedDurationMinutes;
    }

    public String getParkedDurationFull() {
        return parkedDurationFull;
    }

    public void setParkedDurationFull(String parkedDurationFull) {
        this.parkedDurationFull = parkedDurationFull;
    }
}
