package com.GpsTracker.Thinture.dto;
//package com.GpsTracker.Thinture.dto;

public class DeviceLocation {

    private String deviceId;
    private double latitude;
    private double longitude;
    private String timestamp; // You can also use java.sql.Timestamp or java.time.LocalDateTime if needed

    public DeviceLocation(String deviceId, double latitude, double longitude, String timestamp) {
        this.deviceId = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
