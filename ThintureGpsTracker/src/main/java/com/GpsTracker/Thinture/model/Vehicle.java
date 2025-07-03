 package com.GpsTracker.Thinture.model;
import java.sql.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Entity
@Table(name = "vehicle")

public class Vehicle extends BaseEntity {
	private static final Logger logger = LoggerFactory.getLogger(Vehicle.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( nullable = true, name = "deviceID")
    private String deviceID;

    

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

/*
    @JsonIgnore // Prevent serialization of the history field
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VehicleHistory> history;
*/
    @JsonIgnore 
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VehicleHistory> history;

//18/09/2024 10am joins for report  
    
    
   
    //new change 07/01/2025  nullable = false
    // ✅ Read-only reference to Admin entity (mapped by BaseEntity.admin_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Admin admin;
    
    
    @Column(name = "admin_id") // in BaseEntity
    private Long admin_id;
    //new change add dealer add single and dual 07/01/2025  nullable = false
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "superadmin_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SuperAdmin superAdmin;

    // new code 10/09/2024 jothiesh
    
    // new code for createdevice.html 
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  insertable = false, updatable = false)
    private User user;

	@Override public Long getUser_id() { return user_id; }
	@Override public void setUser_id(Long user_id) { this.user_id = user_id; }

	@Column(name = "user_id")
	private Long user_id;


    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dealer_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Dealer dealer;
	
	

	@Column(name = "vehicleNumber", unique = true, nullable = true)
    private String vehicleNumber;

    @Column
    private String vehicleType;

    private String ownerName;
    private String engineNumber;
    private String manufacturer;
    private String model;
    private Date installationDate;
    @Column(name = "serial_no", unique = true)
    private String serialNo;


	    private String technicianName;
	    @Column(name = "imei", unique = true)
	    private String imei;
	    private String simNumber;
	    private String dealerName;
	    private String addressPhone;
	    private String country;
	    private String clientName;
	    
		@Column(name = "superadmin_id")
		private Long superadmin_id;

		@Column(name = "dealer_id")
		private Long dealer_id;


		@Column(name = "driver_id")
		private Long driver_id;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "driver_id", insertable = false, updatable = false)
		private Driver driver;


		@Override public Long getAdmin_id() { return admin_id; }
		@Override public void setAdmin_id(Long admin_id) { this.admin_id = admin_id; }

		@Override public Long getDealer_id() { return dealer_id; }
		@Override public void setDealer_id(Long dealer_id) { this.dealer_id = dealer_id; }
		
		
		@Column(name = "client_id")
		private Long client_id;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "client_id", referencedColumnName = "id", insertable = false, updatable = false)
		private Client client;
		
		
		
		@Override public Long getClient_id() { return client_id; }
		@Override public void setClient_id(Long client_id) { this.client_id = client_id; }

		@Override public Long getSuperadmin_id() { return superadmin_id; }
		@Override public void setSuperadmin_id(Long superadmin_id) { this.superadmin_id = superadmin_id; }

	

		@Override public Long getDriver_id() { return driver_id; }
		@Override public void setDriver_id(Long driver_id) { this.driver_id = driver_id; }

		
		public String getClientName() {
			return clientName;
		}

		public void setClientName(String clientName) {
			this.clientName = clientName;
		}

		

	 
	    public Client getClient() {
			return client;
		}

		public void setClient(Client client) {
			this.client = client;
		}

		// Relationship with Client (A vehicle may be assigned to a Client)
		
		
	
	    
	    
    public Dealer getDealer() {
			return dealer;
		}

		public void setDealer(Dealer dealer) {
			this.dealer = dealer;
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

    public List<VehicleHistory> getHistory() {
        return history;
    }

    public void setHistory(List<VehicleHistory> history) {
        this.history = history;
    }
    
    
    
	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getEngineNumber() {
		return engineNumber;
	}

	public void setEngineNumber(String engineNumber) {
		this.engineNumber = engineNumber;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
  
	  

		public Date getInstallationDate() {
			return installationDate;
		}

		public void setInstallationDate(Date installationDate) {
			this.installationDate = installationDate;
		}

		

		public String getSerialNo() {
			return serialNo;
		}

		public void setSerialNo(String serialNo) {
			this.serialNo = serialNo;
		}

		public String getTechnicianName() {
			return technicianName;
		}

		public void setTechnicianName(String technicianName) {
			this.technicianName = technicianName;
		}

		public String getImei() {
			return imei;
		}

		public void setImei(String imei) {
			this.imei = imei;
		}

		public String getSimNumber() {
			return simNumber;
		}

		public void setSimNumber(String simNumber) {
			this.simNumber = simNumber;
		}

		public String getDealerName() {
			return dealerName;
		}

		public void setDealerName(String dealerName) {
			this.dealerName = dealerName;
		}

		public String getAddressPhone() {
			return addressPhone;
		}

		public void setAddressPhone(String addressPhone) {
			this.addressPhone = addressPhone;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public static Logger getLogger() {
			return logger;
		}
		
		
	    public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}
		
		
		@Column(name = "renewalDate")
		private Date renewalDate;

		@Column(name = "renewed")
		private Boolean renewed = false;

		@Column(name = "renewalRemarks")
		private String renewalRemarks;

		public Date getRenewalDate() {
		    return renewalDate;
		}

		public void setRenewalDate(Date renewalDate) {
		    this.renewalDate = renewalDate;
		}

		public Boolean getRenewed() {
		    return renewed;
		}

		public void setRenewed(Boolean renewed) {
		    this.renewed = renewed;
		}

		public String getRenewalRemarks() {
		    return renewalRemarks;
		}

		public void setRenewalRemarks(String renewalRemarks) {
		    this.renewalRemarks = renewalRemarks;
		}

		@Override
		public String toString() {
		    return "Vehicle{" +
		        "id=" + id +
		        ", vehicleNumber='" + vehicleNumber + '\'' +
		        ", user_id=" + user_id +
		        ", driver_id=" + driver_id +
		        ", serialNo='" + serialNo + '\'' +
		        ", imei='" + imei + '\'' +
		        ", model='" + model + '\'' +
		        ", ownerName='" + ownerName + '\'' +
		        '}';
		}

}