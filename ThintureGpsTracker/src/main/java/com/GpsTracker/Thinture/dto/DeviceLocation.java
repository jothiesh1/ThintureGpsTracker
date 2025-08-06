package com.GpsTracker.Thinture.dto;
//package com.GpsTracker.Thinture.dto;

public class DeviceLocation {

    private String deviceId;
    private double latitude;
    private double longitude;
    private String timestamp; // You can also use java.sql.Timestamp or java.time.LocalDateTime if needed
    private String ignition;
    private String course;
    private String vehicleStatus ;
    private double speed;
    private String additionalData;
    private String sequenceNumber;
   


	public double getSpeed() {
		return speed;
	}


	public void setSpeed(double speed) {
		this.speed = speed;
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


	public String getVehicleStatus() {
		return vehicleStatus;
	}


	public void setVehicleStatus(String vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}


	

	  public DeviceLocation(String deviceId, double latitude, double longitude, String timestamp, 
              String ignition, String vehicleStatus, double speed, 
              String additionalData, String sequenceNumber) {
		  super();
this.deviceId = deviceId;
this.latitude = latitude;
this.longitude = longitude;
this.timestamp = timestamp;
this.ignition = ignition;
this.vehicleStatus = vehicleStatus;
this.speed = speed;
this.additionalData = additionalData;
this.sequenceNumber = sequenceNumber;
}
    public String getIgnition() {
		return ignition;
	}


	public void setIgnition(String ignition) {
		this.ignition = ignition;
	}


	public String getCourse() {
		return course;
	}


	public void setCourse(String course) {
		this.course = course;
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
    @Override
    public String toString() {
        return "DeviceLocation{" +
                "deviceId='" + deviceId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
