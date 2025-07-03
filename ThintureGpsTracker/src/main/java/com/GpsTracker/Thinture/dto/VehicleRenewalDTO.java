package com.GpsTracker.Thinture.dto;

import java.sql.Date;

public class VehicleRenewalDTO {
    private Long id;
    private String vehicleNumber;
    private String deviceID;
    private String driverName;
    private Date installationDate;
    private Date renewalDate;
    private Boolean renewed;
    private String renewalRemarks;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getVehicleNumber() {
		return vehicleNumber;
	}
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public Date getInstallationDate() {
		return installationDate;
	}
	public void setInstallationDate(Date installationDate) {
		this.installationDate = installationDate;
	}
	public Date getRenewalDate() {
		return renewalDate;
	}
	public void setRenewalDate(Date renewalDate) {
		this.renewalDate = renewalDate;
	}
	public Boolean getRenewed() {
		return renewed;
	}
	public void setRenewed(Boolean renewed) {
		this.renewed = renewed;
	}
	public String getRenewalRemarks() {
		return renewalRemarks;
	}
	public void setRenewalRemarks(String renewalRemarks) {
		this.renewalRemarks = renewalRemarks;
	}

    // Getters & Setters
}
