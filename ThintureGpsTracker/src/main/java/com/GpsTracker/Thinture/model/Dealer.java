package com.GpsTracker.Thinture.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "dealers")
public class Dealer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //18/09/2024 10am joins for report  
    
 // Default constructor (required by Hibernate)
    public Dealer() {
    }
    // Constructor for specific use cases (optional)
    public Dealer(Long id) {
        this.id = id;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Admin admin;


    public List<Client> getClients() {
		return clients;
	}
	public void setClients(List<Client> clients) {
		this.clients = clients;
	}
	public List<Driver> getDrivers() {
		return drivers;
	}
	public void setDrivers(List<Driver> drivers) {
		this.drivers = drivers;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "superadmin_id", referencedColumnName = "id", nullable = true)  // Foreign key reference to SuperAdmin
    private SuperAdmin superAdmin;
 // References the Super Admin
    //end here
    
    
    // Additional relationships
    //02/01/2025
    
 
    // Relationship with Vehicles
    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();
 // Relationship with VehicleHistory
    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleHistory> vehicleHistory = new ArrayList<>();

    // Relationship with VehicleLastLocation
    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleLastLocation> vehicleLastLocations = new ArrayList<>();
 //02/01/2025
    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Client> clients = new ArrayList<>();

    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Driver> drivers = new ArrayList<>();
    
    
    public List<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(List<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}

	public List<VehicleHistory> getVehicleHistory() {
		return vehicleHistory;
	}

	public void setVehicleHistory(List<VehicleHistory> vehicleHistory) {
		this.vehicleHistory = vehicleHistory;
	}

	public List<VehicleLastLocation> getVehicleLastLocations() {
		return vehicleLastLocations;
	}

	public void setVehicleLastLocations(List<VehicleLastLocation> vehicleLastLocations) {
		this.vehicleLastLocations = vehicleLastLocations;
	}

	
    

	public List<String> getSerialNumbers() {
		return serialNumbers;
	}

	public void setSerialNumbers(List<String> serialNumbers) {
		this.serialNumbers = serialNumbers;
	}

	private String companyName;
    private String address;
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    private String phone;
    private String country;
    private String password;
    private boolean status;

    @ElementCollection
    private List<String> serialNumbers = new ArrayList<>();

   

    public void addSerial(String serial) {
        this.serialNumbers.add(serial);
    }

	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Admin getAdmin() {
		return admin;
	}
	public void setAdmin(Admin admin) {
		this.admin = admin;
	}
	public SuperAdmin getSuperAdmin() {
		return superAdmin;
	}
	public void setSuperAdmin(SuperAdmin superAdmin) {
		this.superAdmin = superAdmin;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	// toString method for logging
    @Override
    public String toString() {
        return "Dealer{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", country='" + country + '\'' +
                ", status=" + status +
               
                '}';
    }
    private String resetToken; // Add this field

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
	
}