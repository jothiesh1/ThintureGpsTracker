package com.GpsTracker.Thinture.dto;

import java.util.Date;
public class VehicleViolationReportDTO {
    private String deviceId;
    private String vehicleNumber;
    private String vehicleType;
    private String dealerName;
    private String ownerName;
    private String additionalData;
    private String timestamp;
    private String latitude;
    private String longitude;
    private String speed;
    private String status;

    public VehicleViolationReportDTO() {
        this.deviceId = deviceId;
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
    }

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(String additionalData) {
		this.additionalData = additionalData;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    // Getters
   
}

	
	
	
	
	
	/*
	    private Long id;
	    private String deviceId;
	    private String violationType;
	    private Double speed;
	    private String location;
	    private Date violationDate;
	    private String status;

	   

		// Getters and Setters
	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getDeviceId() {
	        return deviceId;
	    }

	    public void setDeviceId(String deviceId) {
	        this.deviceId = deviceId;
	    }

	    public String getViolationType() {
	        return violationType;
	    }

	    public void setViolationType(String violationType) {
	        this.violationType = violationType;
	    }

	    public Double getSpeed() {
	        return speed;
	    }

	    public void setSpeed(Double speed) {
	        this.speed = speed;
	    }

	    public String getLocation() {
	        return location;
	    }

	    public void setLocation(String location) {
	        this.location = location;
	    }

	    public Date getViolationDate() {
	        return violationDate;
	    }

	    public void setViolationDate(Date violationDate) {
	        this.violationDate = violationDate;
	    }

	    public String getStatus() {
	        return status;
	    }

	    public void setStatus(String status) {
	        this.status = status;
	    }
	}

*/