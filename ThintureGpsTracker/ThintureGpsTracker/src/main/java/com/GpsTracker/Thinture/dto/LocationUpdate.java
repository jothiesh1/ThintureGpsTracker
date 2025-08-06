package com.GpsTracker.Thinture.dto;



public class LocationUpdate {
    private double latitude;
    private double longitude;
    private String deviceID;
    private String timestamp;
    private String speed;
    private String ignition;
    private String course;
    private String vehicleStatus;
    private String additionalData;
    private String distanceItervals;
    private String timeIntervals;

    public LocationUpdate(double latitude, double longitude, String deviceID, String timestamp, String speed,
            String ignition, String course, String vehicleStatus, String additionalData, String timeIntervals) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.deviceID = deviceID;
        this.timestamp = timestamp;
        this.speed = speed;
        this.ignition = ignition;
        this.course = course;
        this.vehicleStatus = vehicleStatus;
        this.additionalData = additionalData;
        this.timeIntervals = timeIntervals;
    }

    public String getTimeIntervals() {
        return timeIntervals;
    }

    public void setTimeIntervals(String timeIntervals) {
        this.timeIntervals = timeIntervals;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public String getDistanceItervals() {
        return distanceItervals;
    }

    public void setDistanceItervals(String distanceItervals) {
        this.distanceItervals = distanceItervals;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
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

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getIgnition() {
        return ignition;
    }

    public void setIgnition(String ignition) {
        this.ignition = ignition;
    
}
	
	
	
	@Override
    public String toString() {
        return "LocationUpdate{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", deviceID='" + deviceID + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", speed='" + speed + '\'' +
                ", ignition='" + ignition + '\'' +
                ", course='" + course + '\'' +
                ", vehicleStatus='" + vehicleStatus + '\'' +
                ", additionalData='" + additionalData + '\'' +
                ", distanceItervals='" + distanceItervals + '\'' +
                '}';
    }
	
}


