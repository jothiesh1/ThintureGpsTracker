package com.GpsTracker.Thinture.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
@Table(name = "users")
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Dealer dealer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Client client;

//
    

    @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "superadmin_id", referencedColumnName = "id", nullable = true) // ✅ Foreign key reference to SuperAdmin
    @JsonIgnore // 🔥 this will skip it in JSON
    private SuperAdmin superAdmin;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) // ✅ Fix mappedBy reference
    private List<Vehicle> vehicles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) // ✅ Fix mappedBy reference
    private List<VehicleHistory> vehicleHistory = new ArrayList<>();

    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) // ✅ Fix mappedBy reference
    private List<Driver> drivers = new ArrayList<>();


	
    public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}





	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleLastLocation> vehicleLastLocations = new ArrayList<>();

	
	
	
	
	
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

	public List<Driver> getDrivers() {
		return drivers;
	}

	public void setDrivers(List<Driver> drivers) {
		this.drivers = drivers;
	}

	public void setDealer(Dealer dealer) {
		this.dealer = dealer;
	}





	   @Column(unique = true, nullable = true) // Ensuring uniqueness
	    private String username; 









	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}





	private String companyName;
    private String address;
    private String email;
    private String phone;
    private String country;
    private String password;

    // Getters and Setters
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
    
    private String resetToken; // Add this field

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
}
