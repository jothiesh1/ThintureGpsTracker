package com.GpsTracker.Thinture.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Entity
@Table(name = "vehicle")

public class Vehicle {
	private static final Logger logger = LoggerFactory.getLogger(Vehicle.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = true, name = "deviceID")
    private String deviceID;


    @JsonIgnore // Prevent serialization of the history field
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VehicleHistory> history;

    
//18/09/2024 10am joins for report  
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "id", nullable = false)  // Foreign key reference to Admin
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "superadmin_id", referencedColumnName = "id", nullable = false)  // Foreign key reference to SuperAdmin
    private SuperAdmin superAdmin;
    // Getters and Setters
    
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

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public List<VehicleHistory> getHistory() {
        return history;
    }

    public void setHistory(List<VehicleHistory> history) {
        this.history = history;
    }
    
    
    
    
    
    
    // new code 10/09/2024 jothiesh
    
    // new code for createdevice.html 
    @NotNull
    @Column(unique = true, nullable = false)  // Defines the column constraints
    private String vehicleNumber;

    @Column
    private String vehicleType;

    private String ownerName;
    private String engineNumber;
    private String manufacturer;
    private String model;

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
  
	   private Date installationDate;
	   private String SerialNo;
	    private String technicianName;
	    private  String imei;
	    private String simNumber;
	    private String dealerName;
	    private String addressPhone;
	    private String country;

		public Date getInstallationDate() {
			return installationDate;
		}

		public void setInstallationDate(Date installationDate) {
			this.installationDate = installationDate;
		}

		public String getSerialNo() {
			return SerialNo;
		}

		public void setSerialNo(String serialNo) {
			SerialNo = serialNo;
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
}