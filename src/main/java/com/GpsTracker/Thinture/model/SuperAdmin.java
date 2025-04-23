package com.GpsTracker.Thinture.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class SuperAdmin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "superadmin_id") // optional: keep this if you need a column
    private Long superadmin_id; // self-mapping, just return `id`

    private String email;
    private String password;
    private String country;
    private String phone;
    private String name;
    private String resetToken;
    private boolean status;

 
	@OneToMany(mappedBy = "superAdmin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Admin> admins = new ArrayList<>();

    @OneToMany(mappedBy = "superAdmin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dealer> dealers = new ArrayList<>();

    @OneToMany(mappedBy = "superAdmin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicle = new ArrayList<>();

    // ====== BaseEntity Overrides ======
    @Override public Long getSuperadmin_id() { return this.id; } // or return superadmin_id;
    @Override public void setSuperadmin_id(Long superadmin_id) { this.superadmin_id = superadmin_id; }

    @Override public Long getAdmin_id() { return null; }
    @Override public void setAdmin_id(Long admin_id) {}

    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getResetToken() {
		return resetToken;
	}
	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}
	public List<Admin> getAdmins() {
		return admins;
	}
	public void setAdmins(List<Admin> admins) {
		this.admins = admins;
	}
	public List<Dealer> getDealers() {
		return dealers;
	}
	public void setDealers(List<Dealer> dealers) {
		this.dealers = dealers;
	}
	public List<Vehicle> getVehicle() {
		return vehicle;
	}
	public void setVehicle(List<Vehicle> vehicle) {
		this.vehicle = vehicle;
	}
	@Override public Long getDealer_id() { return null; }
    @Override public void setDealer_id(Long dealer_id) {}

    @Override public Long getClient_id() { return null; }
    @Override public void setClient_id(Long client_id) {}

    @Override public Long getDriver_id() { return null; }
    @Override public void setDriver_id(Long driver_id) {}

    @Override public Long getUser_id() { return null; }
    @Override public void setUser_id(Long user_id) {}

    
    public boolean isStatus() {
 		return status;
 	}
 	public void setStatus(boolean status) {
 		this.status = status;
 	}
    // ====== Getters & Setters ======
    // (You already have all the rest: id, name, email, etc.)
}
