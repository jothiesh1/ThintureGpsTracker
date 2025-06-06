package com.GpsTracker.Thinture.model;



import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "admins",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "email") // ✅ Email must be unique
        // You can add more like: @UniqueConstraint(columnNames = "phone") if needed
    }
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "vehicleHistory"})
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    private String address;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private String country;
    private String password;
    
    private boolean status = true;

    private String resetToken;

    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "superadmin_id", nullable = true)
    private SuperAdmin superAdmin;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dealer> dealers = new ArrayList<>();

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Client> clients = new ArrayList<>();

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Driver> drivers = new ArrayList<>();
    // === BaseEntity Implementation ===
    @Override public Long getAdmin_id() { return id; }
    @Override public void setAdmin_id(Long admin_id) { this.id = admin_id; }

    @Override public Long getDealer_id() { return null; }
    @Override public void setDealer_id(Long dealer_id) { /* no-op */ }

    @Override public Long getClient_id() { return null; }
    @Override public void setClient_id(Long client_id) { /* no-op */ }

    @Override public Long getDriver_id() { return null; }
    @Override public void setDriver_id(Long driver_id) { /* no-op */ }

    @Override public Long getUser_id() { return null; }
    @Override public void setUser_id(Long user_id) { /* no-op */ }

    @Override public Long getSuperadmin_id() {
        return superAdmin != null ? superAdmin.getId() : null;
    }

    @Override public void setSuperadmin_id(Long superadmin_id) {
        // Optional: if you need to fetch the SuperAdmin entity by ID, use a service
        // this.superAdmin = superAdminService.findById(superadmin_id);
    }

    // === Getters & Setters ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public SuperAdmin getSuperAdmin() { return superAdmin; }
    public void setSuperAdmin(SuperAdmin superAdmin) { this.superAdmin = superAdmin; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public List<Dealer> getDealers() { return dealers; }
    public void setDealers(List<Dealer> dealers) { this.dealers = dealers; }

    public List<Client> getClients() { return clients; }
    public void setClients(List<Client> clients) { this.clients = clients; }

    public List<Driver> getDrivers() { return drivers; }
    public void setDrivers(List<Driver> drivers) { this.drivers = drivers; }

    
    
    
    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", country='" + country + '\'' +
                ", status=" + status +
                ", superadmin_id=" + getSuperadmin_id() +
                '}';
    }
}
