package com.GpsTracker.Thinture.model;
import java.sql.Timestamp;
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
import jakarta.persistence.Transient;

//import javax.persistence.*;
import java.sql.Timestamp;
@Entity
@Table(name = "vehicle_history")
public class VehicleHistory  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "device_id", referencedColumnName = "deviceID", nullable = true)
    private Vehicle vehicle;


////18/09/2024 10am joins for report  
//
  @ManyToOne(fetch = FetchType.LAZY)
 @JsonIgnore
 @JoinColumn(name = "superadmin_id", referencedColumnName = "id", nullable = true)  // Foreign key reference to SuperAdmin
    private SuperAdmin superAdmin;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_id", referencedColumnName = "id", insertable = false, updatable = false)
  @Transient // ✅ Prevent Hibernate from mapping this to the DB
  private Admin admin;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dealer_id", referencedColumnName = "id", insertable = false, updatable = false)
  private Dealer dealer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", referencedColumnName = "id", insertable = false, updatable = false)
  private Client client;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "driver_id", referencedColumnName = "id", insertable = false, updatable = false)
  private Driver driver;


    public User getUser() {
	return user;
}

public void setUser(User user) {
	this.user = user;
}


	@Column(name = "timestamp")
    private Timestamp timestamp;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private String course;
    private String additionalData;
    private String sequenceNumber;
    private String ignition ;
    
    @Column(name = "vehicleStatus")
    private String vehicleStatus;

    
    private String status;
    
private String timeIntervals ;
	
	private String distanceItervals ;
	
	
	private String gsmStrength;
   

	public String getTimeIntervals() {
		return timeIntervals;
	}

	public void setTimeIntervals(String timeIntervals) {
		this.timeIntervals = timeIntervals;
	}

	public String getDistanceItervals() {
		return distanceItervals;
	}

	public void setDistanceItervals(String distanceItervals) {
		this.distanceItervals = distanceItervals;
	}

	public String getGsmStrength() {
		return gsmStrength;
	}

	public void setGsmStrength(String gsmStrength) {
		this.gsmStrength = gsmStrength;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	

    
    
    
    // Getters and Setters
   // Getters and Setters
    
//    public Admin getAdmin() {
//		return admin;
//	}
//
//	public void setAdmin(Admin admin) {
//		this.admin = admin;
//	}
//
//	public SuperAdmin getSuperAdmin() {
//		return superAdmin;
//	}
//
//	public void setSuperAdmin(SuperAdmin superAdmin) {
//		this.superAdmin = superAdmin;
//	}
    
    public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Dealer getDealer() {
		return dealer;
	}

	public void setDealer(Dealer dealer) {
		this.dealer = dealer;
	}

	public SuperAdmin getSuperAdmin() {
		return superAdmin;
	}

	public void setSuperAdmin(SuperAdmin superAdmin) {
		this.superAdmin = superAdmin;
	}

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public Long getId() {
        return id;
    }

    public String getVehicleStatus() {
		return vehicleStatus;
	}

	public void setVehicleStatus(String vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}

	public String getIgnition() {
		return ignition;
	}

	public void setIgnition(String ignition) {
		this.ignition = ignition;
	}

	public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

  
  //Bidirectional relationship with ViolationReport
    //@OneToMany(mappedBy = "vehicleHistory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  //  @JsonIgnore
  //  private List<ViolationReport> violationReports;


//public List<ViolationReport> getViolationReports() {
	//return violationReports;
//}

//public void setViolationReports(List<ViolationReport> violationReports) {
//	this.violationReports = violationReports;
//}


//panic code 17/10/2024

public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}


// Panic field to store 0 or 1
@Column(name = "panic")
private Integer panic;

public Integer getPanic() {
	return panic;
}

public void setPanic(Integer panic) {
	this.panic = panic;
}


// Optional: Convenience method to interpret panic as a boolean
/*
 * public boolean isPanicActive() { return panic != null && panic == 1; }
 */






private String serialNo;





@Column(name = "dealer_id")  // ✅ matches the column
private Long dealerId;       // ✅ use Long, not String

public String getSerialNo() {
	return serialNo;
}

public void setSerialNo(String serialNo) {
	this.serialNo = serialNo;
}





public String getDealerName() {
	return dealerName;
}

public void setDealerName(String dealerName) {
	this.dealerName = dealerName;
}
private String dealerName;



@Column(name = "imei")
private String imei;

public String getImei() {
    return imei;
}

public void setImei(String imei) {
    this.imei = imei;
}


}
