package com.GpsTracker.Thinture.model;
import java.sql.Date;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "drivers")
public class Driver {

   
    // 🔹 Relationship with Admin
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "id", nullable = true)
    @JsonIgnore
    private Admin admin;

    // 🔹 Relationship with SuperAdmin
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "superadmin_id", referencedColumnName = "id", nullable = true)
    @JsonIgnore
    private SuperAdmin superAdmin;

    // 🔹 Relationship with Dealer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id", referencedColumnName = "id", nullable = true)
    @JsonIgnore
    private Dealer dealer;

    // 🔹 Relationship with Client
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id", nullable = true)
    @JsonIgnore
    private Client client;

    // 🔹 Relationship with User (Added this)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true)
    @JsonIgnore
    private User user;
    public Dealer getDealer() {
		return dealer;
	}
	public void setDealer(Dealer dealer) {
		this.dealer = dealer;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
    
    // Default constructor
    public Driver() {
    }
 // Parameterized constructor (optional, if you need it)
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String ivmsKey;
        public String getIvmsKey() {
			return ivmsKey;
		}
		public void setIvmsKey(String ivmsKey) {
			this.ivmsKey = ivmsKey;
		}
		private String firstName;
        private String secondName;
        private String familyName;
        private String employeeNumber;
        private String phone;
        private String department;
        @Column(nullable = false)
        private String country;
        private String licenseNumber;
        private String defensivePermit;
        private Date permitExpiry;
        private String managerName;
        private String managerEmail;
        private String focalPointName;
        private String focalPointEmail;

		public Driver(Admin admin, SuperAdmin superAdmin, Long id, String firstName, String secondName,
				String familyName, String employeeNumber, String phone, String department, String country,
				String licenseNumber, String defensivePermit, Date permitExpiry, String managerName,
				String managerEmail, String focalPointName, String focalPointEmail) {
			super();
			this.admin = admin;
			this.superAdmin = superAdmin;
			this.id = id;
			this.firstName = firstName;
			this.secondName = secondName;
			this.familyName = familyName;
			this.employeeNumber = employeeNumber;
			this.phone = phone;
			this.department = department;
			this.country = country;
			this.licenseNumber = licenseNumber;
			this.defensivePermit = defensivePermit;
			this.permitExpiry = permitExpiry;
			this.managerName = managerName;
			this.managerEmail = managerEmail;
			this.focalPointName = focalPointName;
			this.focalPointEmail = focalPointEmail;
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
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getFirstName() {
			return firstName;
		}
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
		public String getSecondName() {
			return secondName;
		}
		public void setSecondName(String secondName) {
			this.secondName = secondName;
		}
		public String getFamilyName() {
			return familyName;
		}
		public void setFamilyName(String familyName) {
			this.familyName = familyName;
		}
		public String getEmployeeNumber() {
			return employeeNumber;
		}
		public void setEmployeeNumber(String employeeNumber) {
			this.employeeNumber = employeeNumber;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getDepartment() {
			return department;
		}
		public void setDepartment(String department) {
			this.department = department;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		public String getLicenseNumber() {
			return licenseNumber;
		}
		public void setLicenseNumber(String licenseNumber) {
			this.licenseNumber = licenseNumber;
		}
		public String getDefensivePermit() {
			return defensivePermit;
		}
		public void setDefensivePermit(String defensivePermit) {
			this.defensivePermit = defensivePermit;
		}
		public Date getPermitExpiry() {
			return permitExpiry;
		}
		public void setPermitExpiry(Date permitExpiry) {
			this.permitExpiry = permitExpiry;
		}
		public String getManagerName() {
			return managerName;
		}
		public void setManagerName(String managerName) {
			this.managerName = managerName;
		}
		public String getManagerEmail() {
			return managerEmail;
		}
		public void setManagerEmail(String managerEmail) {
			this.managerEmail = managerEmail;
		}
		public String getFocalPointName() {
			return focalPointName;
		}
		public void setFocalPointName(String focalPointName) {
			this.focalPointName = focalPointName;
		}
		public String getFocalPointEmail() {
			return focalPointEmail;
		}
		public void setFocalPointEmail(String focalPointEmail) {
			this.focalPointEmail = focalPointEmail;
		}
        
        // Getters and setters
    }
