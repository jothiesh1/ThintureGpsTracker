package com.GpsTracker.Thinture.dto;

import java.sql.Timestamp;
import java.util.List;



import java.sql.Timestamp;



public class DriverRagReportDTO {

    private String deviceID;
    private String vehicleNumber;
    private String vehicleType;
    private String dealerName;
    private String ownerName;
    private String additionalData;
    private Timestamp timestamp;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private String status;
    private Long driverId;
    private String rfidCode;
    private Double maxSpeed;
    private Double totalDistance; // in KM

    private double totalScore; // expose this in API

    
    // Optional: constructor for mapping SQL result directly
    public DriverRagReportDTO(String deviceID, String vehicleNumber, String vehicleType,
                              String dealerName, String ownerName, String additionalData,
                              Timestamp timestamp, Double latitude, Double longitude,
                              Double speed, String status, Long driverId, String rfidCode ,Double maxSpeed , Double totalDistance) {
        this.deviceID = deviceID;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.dealerName = dealerName;
        this.ownerName = ownerName;
        this.additionalData = additionalData;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.status = status;
        this.driverId = driverId;
        this.rfidCode = rfidCode;
        
        this.maxSpeed = maxSpeed;
        this.totalDistance = totalDistance;
    }

    public double getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(double totalScore) {
		this.totalScore = totalScore;
	}

	public Double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(Double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public Double getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(Double totalDistance) {
		this.totalDistance = totalDistance;
	}

	// Getters and setters
    public String getDeviceID() { return deviceID; }
    public void setDeviceID(String deviceID) { this.deviceID = deviceID; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getDealerName() { return dealerName; }
    public void setDealerName(String dealerName) { this.dealerName = dealerName; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getAdditionalData() { return additionalData; }
    public void setAdditionalData(String additionalData) { this.additionalData = additionalData; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getSpeed() { return speed; }
    public void setSpeed(Double speed) { this.speed = speed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public String getRfidCode() { return rfidCode; }
    public void setRfidCode(String rfidCode) { this.rfidCode = rfidCode; }
}

