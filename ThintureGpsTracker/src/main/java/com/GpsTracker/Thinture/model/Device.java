package com.GpsTracker.Thinture.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date installationDate;
   private String SerialNo;
    private String technicianName;
    private  String imei;
    private String simNumber;
    private String dealerName;
    private String addressPhone;
    private String country;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getInstallationDate() {
		return installationDate;
	}
	public void setInstallationDate(Date installationDate) {
		this.installationDate = installationDate;
	}
//	
	public String getTechnicianName() {
		return technicianName;
	}
	public String getSerialNo() {
		return SerialNo;
	}
	public void setSerialNo(String serialNo) {
		SerialNo = serialNo;
	}
	public void setTechnicianName(String technicianName) {
		this.technicianName = technicianName;
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
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public String getAddressPhone() {
		return addressPhone;
	}
	public void setAddressPhone(String addressPhone) {
		this.addressPhone = addressPhone;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
}
	/*
	//     panic code 17/10/2024

	// Panic field to store 0 or 1
	@Column(name = "panic")
	private Integer panic;

	// Getters and Setters
	public Integer getPanic() {
	    return panic;
	}

	public void setPanic(Integer panic) {
	    this.panic = panic;
	}

//	// Optional: Convenience method to interpret panic as a boolean
//	public boolean isPanicActive() {
//	    return panic != null && panic == 1;
//	}
 * */
 
	

    
