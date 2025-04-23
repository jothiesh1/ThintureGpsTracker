package com.GpsTracker.Thinture.dto;

import com.GpsTracker.Thinture.model.Device;

public class DeviceDTO {

    private String serialNo;
    private String imei;
    private String dealerName;
    private String simNumber;
    private String installerName;
    private String dealerPhone;
    private String country;
    private Long userId;

    // Constructor to map from the Device entity
    public DeviceDTO(Device device) {
        this.serialNo = device.getSerialNo();
        this.imei = device.getImei();
        this.dealerName = device.getDealerName();
        this.simNumber = device.getSimNumber();
        this.country = device.getCountry();
    }

    // Getters and setters
    public String getSerialNo() { return serialNo; }
    public void setSerialNo(String serialNo) { this.serialNo = serialNo; }

    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }

    public String getDealerName() { return dealerName; }
    public void setDealerName(String dealerName) { this.dealerName = dealerName; }

    public String getSimNumber() { return simNumber; }
    public void setSimNumber(String simNumber) { this.simNumber = simNumber; }

    public String getInstallerName() { return installerName; }
    public void setInstallerName(String installerName) { this.installerName = installerName; }

    public String getDealerPhone() { return dealerPhone; }
    public void setDealerPhone(String dealerPhone) { this.dealerPhone = dealerPhone; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
