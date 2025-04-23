package com.GpsTracker.Thinture.dto;

import java.time.LocalDate;

public class allUserDTO {
    private Long id;
    private String companyName;
    private String email;
    private String address;
    private String phone;
    private String country;
    private boolean status;

    // ✅ Driver-specific fields
    private String license;
    private String licenseType;
    private String rfid;
    private String ddp;
    private LocalDate ddpExpiry;
    private String vehicle;
    private LocalDate dob;

    public allUserDTO(Long id, String companyName, String email, String address, String phone,
                      String country, boolean status) {
        this.id = id;
        this.companyName = companyName;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.country = country;
        this.status = status;
    }

    // ✅ Overloaded constructor for Driver
    public allUserDTO(Long id, String companyName, String email, String address, String phone,
                      String country, boolean status, String license, String licenseType,
                      String rfid, String ddp, LocalDate ddpExpiry, String vehicle, LocalDate dob) {
        this(id, companyName, email, address, phone, country, status);
        this.license = license;
        this.licenseType = licenseType;
        this.rfid = rfid;
        this.ddp = ddp;
        this.ddpExpiry = ddpExpiry;
        this.vehicle = vehicle;
        this.dob = dob;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
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

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
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

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}
    
}