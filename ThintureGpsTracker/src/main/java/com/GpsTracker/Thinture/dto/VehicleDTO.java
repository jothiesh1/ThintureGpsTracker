package com.GpsTracker.Thinture.dto;

import java.sql.Date;

public class VehicleDTO {

    private String deviceID;
    private String serialNo;
    private String ownerName;
    private String imei;
    private String simNumber;
    private String vehicleNumber;
    private String engineNumber;
    private String vehicleType;
    private String model;
    private String technicianName;
    private String dealerName;
    private String addressPhone;
    private String country;
    private Long userId;
    private Long driverId;
    private Date installationDate;
    private Date renewalDate;
    
    private Long user_id;
    
    private Long driver_id;

    // ✅ Constructor for existing usage
    public VehicleDTO(String deviceID, String ownerName, String imei, String simNumber,
                      String vehicleNumber, String engineNumber, String model, String vehicleType,
                      Date installationDate, Date renewalDate ,  String serialNo) {
        this.deviceID = deviceID;
        this.ownerName = ownerName;
        this.imei = imei;
        this.simNumber = simNumber;
        this.vehicleNumber = vehicleNumber;
        this.engineNumber = engineNumber;
        this.model = model;
        this.vehicleType = vehicleType;
        this.installationDate = installationDate;
        this.renewalDate = renewalDate;
       this.serialNo =serialNo;
    }

    public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Long getDriver_id() {
		return driver_id;
	}

	public void setDriver_id(Long driver_id) {
		this.driver_id = driver_id;
	}

	// ✅ Default constructor (important for @RequestBody deserialization)
    public VehicleDTO() {}

    // ✅ Getters and setters for all fields

    public String getDeviceID() { return deviceID; }
    public void setDeviceID(String deviceID) { this.deviceID = deviceID; }

    public String getSerialNo() { return serialNo; }
    public void setSerialNo(String serialNo) { this.serialNo = serialNo; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }

    public String getSimNumber() { return simNumber; }
    public void setSimNumber(String simNumber) { this.simNumber = simNumber; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getEngineNumber() { return engineNumber; }
    public void setEngineNumber(String engineNumber) { this.engineNumber = engineNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getTechnicianName() { return technicianName; }
    public void setTechnicianName(String technicianName) { this.technicianName = technicianName; }

    public String getDealerName() { return dealerName; }
    public void setDealerName(String dealerName) { this.dealerName = dealerName; }

    public String getAddressPhone() { return addressPhone; }
    public void setAddressPhone(String addressPhone) { this.addressPhone = addressPhone; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public Date getInstallationDate() { return installationDate; }
    public void setInstallationDate(Date installationDate) { this.installationDate = installationDate; }

    public Date getRenewalDate() { return renewalDate; }
    public void setRenewalDate(Date renewalDate) { this.renewalDate = renewalDate; }
}
