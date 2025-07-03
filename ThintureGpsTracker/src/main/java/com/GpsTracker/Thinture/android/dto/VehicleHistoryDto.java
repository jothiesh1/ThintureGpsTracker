package com.GpsTracker.Thinture.android.dto;

import java.sql.Timestamp;

public class VehicleHistoryDto {

    private Long id;
    private String deviceID;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private String course;
    private String vehicleStatus;
    private String ignition;
    private String status;
    private String timeIntervals;
    private String distanceItervals;
    private String gsmStrength;
    private String sequenceNumber;
    private Integer panic;
    private String serialNo;
    private String dealerName;
    private String imei;
    private Timestamp timestamp;

    // ✅ Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDeviceID() { return deviceID; }
    public void setDeviceID(String deviceID) { this.deviceID = deviceID; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getSpeed() { return speed; }
    public void setSpeed(Double speed) { this.speed = speed; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getVehicleStatus() { return vehicleStatus; }
    public void setVehicleStatus(String vehicleStatus) { this.vehicleStatus = vehicleStatus; }

    public String getIgnition() { return ignition; }
    public void setIgnition(String ignition) { this.ignition = ignition; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimeIntervals() { return timeIntervals; }
    public void setTimeIntervals(String timeIntervals) { this.timeIntervals = timeIntervals; }

    public String getDistanceItervals() { return distanceItervals; }
    public void setDistanceItervals(String distanceItervals) { this.distanceItervals = distanceItervals; }

    public String getGsmStrength() { return gsmStrength; }
    public void setGsmStrength(String gsmStrength) { this.gsmStrength = gsmStrength; }

    public String getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(String sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public Integer getPanic() { return panic; }
    public void setPanic(Integer panic) { this.panic = panic; }

    public String getSerialNo() { return serialNo; }
    public void setSerialNo(String serialNo) { this.serialNo = serialNo; }

    public String getDealerName() { return dealerName; }
    public void setDealerName(String dealerName) { this.dealerName = dealerName; }

    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
