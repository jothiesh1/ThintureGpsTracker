package com.GpsTracker.Thinture.dto;

import java.sql.Timestamp;

public class DriverRagReportDTO {
    private String deviceId;
    private String vehicleNumber;
    private String vehicleType;
    private String dealerName;
    private String ownerName;
    private String rfidCode;
    private Timestamp timestamp;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private String status;
    private String additionalData;
    
    // Driver specific fields
    private Long driverId;
    private String driverName;
    private String driverEmail;
    private String driverRfid;
    
    // RAG Performance fields
    private Double totalScore;
    private String ragStatus; // GREEN, AMBER, RED
    private Double maxSpeed;
    private Double totalDistance;
    private Integer violationCount;
    
    // Performance metrics
    private Integer speedingViolations;
    private Integer harshAccelerationCount;
    private Integer harshBrakingCount;
    private Integer sharpTurnCount; // ADD THIS FIELD
    private Double speedingTimeInSeconds;

    // Constructors
    public DriverRagReportDTO() {}

    public DriverRagReportDTO(String deviceId, String vehicleNumber, String vehicleType, 
                              String dealerName, String ownerName, String rfidCode,
                              Timestamp timestamp, Double latitude, Double longitude, 
                              Double speed, String status, String additionalData,
                              Long driverId, String driverName, String driverEmail, String driverRfid,
                              Double totalScore, Double maxSpeed, Double totalDistance) {
        this.deviceId = deviceId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.dealerName = dealerName;
        this.ownerName = ownerName;
        this.rfidCode = rfidCode;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.status = status;
        this.additionalData = additionalData;
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverEmail = driverEmail;
        this.driverRfid = driverRfid;
        this.totalScore = totalScore;
        this.maxSpeed = maxSpeed;
        this.totalDistance = totalDistance;
        
        // Calculate RAG status based on score
        if (totalScore <= 1.0) {
            this.ragStatus = "GREEN";
        } else if (totalScore <= 3.0) {
            this.ragStatus = "AMBER";
        } else {
            this.ragStatus = "RED";
        }
    }

    // Getters and Setters
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getDealerName() { return dealerName; }
    public void setDealerName(String dealerName) { this.dealerName = dealerName; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getRfidCode() { return rfidCode; }
    public void setRfidCode(String rfidCode) { this.rfidCode = rfidCode; }

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

    public String getAdditionalData() { return additionalData; }
    public void setAdditionalData(String additionalData) { this.additionalData = additionalData; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverEmail() { return driverEmail; }
    public void setDriverEmail(String driverEmail) { this.driverEmail = driverEmail; }

    public String getDriverRfid() { return driverRfid; }
    public void setDriverRfid(String driverRfid) { this.driverRfid = driverRfid; }

    public Double getTotalScore() { return totalScore; }
    public void setTotalScore(Double totalScore) { 
        this.totalScore = totalScore;
        // Update RAG status when score changes
        if (totalScore <= 1.0) {
            this.ragStatus = "GREEN";
        } else if (totalScore <= 3.0) {
            this.ragStatus = "AMBER";
        } else {
            this.ragStatus = "RED";
        }
    }

    public String getRagStatus() { return ragStatus; }
    public void setRagStatus(String ragStatus) { this.ragStatus = ragStatus; }

    public Double getMaxSpeed() { return maxSpeed; }
    public void setMaxSpeed(Double maxSpeed) { this.maxSpeed = maxSpeed; }

    public Double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }

    public Integer getViolationCount() { return violationCount; }
    public void setViolationCount(Integer violationCount) { this.violationCount = violationCount; }

    public Integer getSpeedingViolations() { return speedingViolations; }
    public void setSpeedingViolations(Integer speedingViolations) { this.speedingViolations = speedingViolations; }

    public Integer getHarshAccelerationCount() { return harshAccelerationCount; }
    public void setHarshAccelerationCount(Integer harshAccelerationCount) { this.harshAccelerationCount = harshAccelerationCount; }

    public Integer getHarshBrakingCount() { return harshBrakingCount; }
    public void setHarshBrakingCount(Integer harshBrakingCount) { this.harshBrakingCount = harshBrakingCount; }

    // ADD THIS GETTER AND SETTER FOR SHARP TURN COUNT
    public Integer getSharpTurnCount() { return sharpTurnCount; }
    public void setSharpTurnCount(Integer sharpTurnCount) { this.sharpTurnCount = sharpTurnCount; }

    public Double getSpeedingTimeInSeconds() { return speedingTimeInSeconds; }
    public void setSpeedingTimeInSeconds(Double speedingTimeInSeconds) { this.speedingTimeInSeconds = speedingTimeInSeconds; }
}