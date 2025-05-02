package com.GpsTracker.Thinture.model;

import java.util.ArrayList;
import java.util.List;

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
@Table(name = "Client")
public class Client extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hybrid ID Fields
    @Column(name = "admin_id")
    private Long admin_id;

    @Column(name = "dealer_id")
    private Long dealer_id;

    @Column(name = "superadmin_id")
    private Long superadmin_id;

    @Column(name = "driver_id")
    private Long driver_id;

    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "client_id")
    private Long client_id;

    // Entity References (LAZY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Dealer dealer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "superadmin_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SuperAdmin superAdmin;

    // Relationships
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleHistory> vehicleHistory = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Driver> drivers = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleLastLocation> vehicleLastLocations = new ArrayList<>();

    // Login Fields
    @Column(unique = true, nullable = true)
    private String email;

    private String companyName;
    private String address;
    private String phone;
    private String country;
    private String password;
    private boolean status = true;

    private String resetToken;

    // === Hybrid Mapping Accessors (from BaseEntity) ===
    @Override public Long getAdmin_id() { return admin_id; }
    @Override public void setAdmin_id(Long admin_id) { this.admin_id = admin_id; }

    @Override public Long getDealer_id() { return dealer_id; }
    @Override public void setDealer_id(Long dealer_id) { this.dealer_id = dealer_id; }

    @Override public Long getSuperadmin_id() { return superadmin_id; }
    @Override public void setSuperadmin_id(Long superadmin_id) { this.superadmin_id = superadmin_id; }

    @Override public Long getClient_id() { return client_id; }
    @Override public void setClient_id(Long client_id) { this.client_id = client_id; }

    @Override public Long getDriver_id() { return driver_id; }
    @Override public void setDriver_id(Long driver_id) { this.driver_id = driver_id; }

    @Override public Long getUser_id() { return user_id; }
    @Override public void setUser_id(Long user_id) { this.user_id = user_id; }

    // === Standard Getters & Setters ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }

    public Dealer getDealer() { return dealer; }
    public void setDealer(Dealer dealer) { this.dealer = dealer; }

    public SuperAdmin getSuperAdmin() { return superAdmin; }
    public void setSuperAdmin(SuperAdmin superAdmin) { this.superAdmin = superAdmin; }

    public List<Vehicle> getVehicles() { return vehicles; }
    public void setVehicles(List<Vehicle> vehicles) { this.vehicles = vehicles; }

    public List<VehicleHistory> getVehicleHistory() { return vehicleHistory; }
    public void setVehicleHistory(List<VehicleHistory> vehicleHistory) { this.vehicleHistory = vehicleHistory; }

    public List<Driver> getDrivers() { return drivers; }
    public void setDrivers(List<Driver> drivers) { this.drivers = drivers; }

    public List<VehicleLastLocation> getVehicleLastLocations() { return vehicleLastLocations; }
    public void setVehicleLastLocations(List<VehicleLastLocation> vehicleLastLocations) {
        this.vehicleLastLocations = vehicleLastLocations;
    }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    // === toString ===
    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", admin_id=" + admin_id +
                ", dealer_id=" + dealer_id +
                ", superadmin_id=" + superadmin_id +
                ", client_id=" + client_id +
                ", country='" + country + '\'' +
                '}';
    }
}
