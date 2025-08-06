package com.GpsTracker.Thinture.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;


@Entity
@Table(
    name = "newdrivers",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "email") // ✅ Ensure email is unique
    }
)
public class Driver extends BaseEntity {

  

    @Column(nullable = false, unique = true)
    private String email;
	 
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "superadmin_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private SuperAdmin superAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Dealer dealer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    private String ddp;
    private LocalDate ddpExpiry;
    private String vehicle;
    private String rfid;
    private String country;
    private String fullName;
    private LocalDate dob;
    private String contact;
   
    private String address;
    private String license;
    private String licenseType;
    private boolean status = true;


    // === Getters and Setters ===

    @Override
    public Long getAdmin_id() { return admin_id; }
    @Override
    public void setAdmin_id(Long admin_id) { this.admin_id = admin_id; }

    @Override
    public Long getDealer_id() { return dealer_id; }
    @Override
    public void setDealer_id(Long dealer_id) { this.dealer_id = dealer_id; }

    @Override
    public Long getSuperadmin_id() { return superadmin_id; }
    @Override
    public void setSuperadmin_id(Long superadmin_id) { this.superadmin_id = superadmin_id; }

    @Override
    public Long getDriver_id() { return driver_id; }
    @Override
    public void setDriver_id(Long driver_id) { this.driver_id = driver_id; }

    @Override
    public Long getUser_id() { return user_id; }
    @Override
    public void setUser_id(Long user_id) { this.user_id = user_id; }

    @Override
    public Long getClient_id() { return client_id; }
    @Override
    public void setClient_id(Long client_id) { this.client_id = client_id; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }

    public SuperAdmin getSuperAdmin() { return superAdmin; }
    public void setSuperAdmin(SuperAdmin superAdmin) { this.superAdmin = superAdmin; }

    public Dealer getDealer() { return dealer; }
    public void setDealer(Dealer dealer) { this.dealer = dealer; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getDdp() { return ddp; }
    public void setDdp(String ddp) { this.ddp = ddp; }

    public LocalDate getDdpExpiry() { return ddpExpiry; }
    public void setDdpExpiry(LocalDate ddpExpiry) { this.ddpExpiry = ddpExpiry; }

    public String getVehicle() { return vehicle; }
    public void setVehicle(String vehicle) { this.vehicle = vehicle; }

    public String getRfid() { return rfid; }
    public void setRfid(String rfid) { this.rfid = rfid; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }

    public String getLicenseType() { return licenseType; }
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
} 