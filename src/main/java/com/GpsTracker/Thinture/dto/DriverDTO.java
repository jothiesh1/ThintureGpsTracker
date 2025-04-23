package com.GpsTracker.Thinture.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DriverDTO {

    private String fullName;
    private LocalDate dob;
    private String contact;
    private String email;
    private String address;
    private String license;
    private String licenseType;
    private String ddp;
    private LocalDate ddpExpiry;
    private String vehicle;
    private String rfid;
    public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public LocalDate getDob() {
		return dob;
	}
	public void setDob(LocalDate dob) {
		this.dob = dob;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getLicenseType() {
		return licenseType;
	}
	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}
	public String getDdp() {
		return ddp;
	}
	public void setDdp(String ddp) {
		this.ddp = ddp;
	}
	public LocalDate getDdpExpiry() {
		return ddpExpiry;
	}
	public void setDdpExpiry(LocalDate ddpExpiry) {
		this.ddpExpiry = ddpExpiry;
	}
	public String getVehicle() {
		return vehicle;
	}
	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}
	public String getRfid() {
		return rfid;
	}
	public void setRfid(String rfid) {
		this.rfid = rfid;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	private String country;
}