package com.GpsTracker.Thinture.dto;

import java.sql.Date;

public class VehicleDTO {
	private String deviceID;
	private String ownerName;
	private String imei;
	private String simNumber;
	private String vehicleNumber;
	private String engineNumber;
	private String vehicleType;
	private String model;
	private Date installationDate ;
	private Date renewalDate ;
    // ✅ Constructor to Convert from Entity
    public VehicleDTO(String deviceID, String ownerName, String imei, String simNumber, String vehicleNumber, String engineNumber, String model, String vehicleType ,Date installationDate ,Date renewalDate) {
        this.deviceID = deviceID;
        this.ownerName = ownerName;
        this.imei = imei;
        this.simNumber =simNumber;
        this.model = model;
        
        this.vehicleType = vehicleType;
      
        this.vehicleNumber = vehicleNumber;
        this.engineNumber = engineNumber;
        
        this.installationDate= installationDate;
        this.renewalDate = renewalDate;
    }

    public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getSimNumber() {
		return simNumber;
	}

	public void setSimNumber(String simNumber) {
		this.simNumber = simNumber;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getEngineNumber() {
		return engineNumber;
	}

	public void setEngineNumber(String engineNumber) {
		this.engineNumber = engineNumber;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getModel() {
		return model;
	}

	public Date getRenewalDate() {
		return renewalDate;
	}

	public void setRenewalDate(Date renewalDate) {
		this.renewalDate = renewalDate;
	}

	public void setModel(String model) {
		this.model = model;
	}

	

	public Date getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(Date installationDate) {
		this.installationDate = installationDate;
	}

}
