package com.GpsTracker.Thinture.model;

import java.io.Serializable;

public class GpsData implements Serializable {
    private String deviceID;
    private String timestamp;
    private String dataValidity;
    private String status;
    private String latitude;
    private String longitude;
    private String speed;
    private String course;
    private String additionalData;
    private String sequenceNumber;

    // Getters and Setters

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDataValidity() {
        return dataValidity;
    }

    public void setDataValidity(String dataValidity) {
        this.dataValidity = dataValidity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    // Convert latitude and longitude to double
    public double getLatitudeAsDouble() {
        return convertCoordinate(latitude);
    }

    public double getLongitudeAsDouble() {
        return convertCoordinate(longitude);
    }

    private double convertCoordinate(String coordinate) {
        if (coordinate == null || coordinate.isEmpty()) {
            throw new IllegalArgumentException("Invalid coordinate");
        }
        String value = coordinate.substring(0, coordinate.length() - 1);
        return Double.parseDouble(value);
    }

    @Override
    public String toString() {
        return "GpsData{" +
                "DeviceID='" + deviceID + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", DataValidity='" + dataValidity + '\'' +
                ", status='" + status + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", speed='" + speed + '\'' +
                '}';
    }
}
