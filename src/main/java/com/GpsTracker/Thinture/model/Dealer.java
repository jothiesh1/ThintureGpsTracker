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

    @Column(name = "admin_id")
    private Long admin_id;

    @Column(name = "superadmin_id")
    private Long superadmin_id;

    // === Hybrid Entity Mappings ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "superadmin_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SuperAdmin superAdmin;

    // === Relationships ===
    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();

    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleHistory> vehicleHistory = new ArrayList<>();

    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleLastLocation> vehicleLastLocations = new ArrayList<>();

    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Client> clients = new ArrayList<>();

    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Driver> drivers = new ArrayList<>();

    // === Attributes ===
    private String companyName;
    private String address;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    private String phone;
    private String country;
    private String password;
    private boolean status = true;


    @ElementCollection
    private List<String> serialNumbers = new ArrayList<>();

    private String resetToken;

    // === Getters & Setters ===
    @Override public Long getAdmin_id() { return admin_id; }
    @Override public void setAdmin_id(Long admin_id) { this.admin_id = admin_id; }

    @Override public Long getSuperadmin_id() { return superadmin_id; }
    @Override public void setSuperadmin_id(Long superadmin_id) { this.superadmin_id = superadmin_id; }

    @Override public Long getDealer_id() { return id; }
    @Override public void setDealer_id(Long dealer_id) { this.id = dealer_id; }

    @Override public Long getClient_id() { return null; }
    @Override public void setClient_id(Long client_id) { /* not applicable */ }

    @Override public Long getDriver_id() { return null; }
    @Override public void setDriver_id(Long driver_id) { /* not applicable */ }

    @Override public Long getUser_id() { return null; }
    @Override public void setUser_id(Long user_id) { /* not applicable */ }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }

    public SuperAdmin getSuperAdmin() { return superAdmin; }
    public void setSuperAdmin(SuperAdmin superAdmin) { this.superAdmin = superAdmin; }

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

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public List<String> getSerialNumbers() { return serialNumbers; }
    public void setSerialNumbers(List<String> serialNumbers) { this.serialNumbers = serialNumbers; }

    public void addSerial(String serial) {
        this.serialNumbers.add(serial);
    }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public List<Vehicle> getVehicles() { return vehicles; }
    public void setVehicles(List<Vehicle> vehicles) { this.vehicles = vehicles; }

    public List<VehicleHistory> getVehicleHistory() { return vehicleHistory; }
    public void setVehicleHistory(List<VehicleHistory> vehicleHistory) { this.vehicleHistory = vehicleHistory; }

    public List<VehicleLastLocation> getVehicleLastLocations() { return vehicleLastLocations; }
    public void setVehicleLastLocations(List<VehicleLastLocation> vehicleLastLocations) { this.vehicleLastLocations = vehicleLastLocations; }

    public List<Client> getClients() { return clients; }
    public void setClients(List<Client> clients) { this.clients = clients; }

    public List<Driver> getDrivers() { return drivers; }
    public void setDrivers(List<Driver> drivers) { this.drivers = drivers; }

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
}
