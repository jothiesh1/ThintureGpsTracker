package com.GpsTracker.Thinture.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;



@Entity
@Table(name = "vehicle")
public class vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deviceID")
    private String deviceID;

    @Column(name = "timestamp")
    private String timestamp;

    @Column(name = "dataValidity")
    private String dataValidity;

    @Column(name = "status")
    private String status;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "speed")
    private String speed;

    @Column(name = "course")
    private String course;

    @Column(name = "additionalData")
    private String additionalData;

    @Column(name = "vehicle_type")
    private String vehicleType;

    // Getters and Setters






	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	private String sequenceNumber;
 
    
    // tempory code 
  
  
    
    // Getters and setters
    // ...
    
    
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	
}
