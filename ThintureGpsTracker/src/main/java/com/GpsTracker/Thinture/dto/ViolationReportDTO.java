package com.GpsTracker.Thinture.dto;

import java.util.Date;

public class ViolationReportDTO {
	
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

