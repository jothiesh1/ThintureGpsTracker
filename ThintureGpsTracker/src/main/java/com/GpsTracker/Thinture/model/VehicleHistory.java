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
//import javax.persistence.*;
import java.sql.Timestamp;
@Entity
@Table(name = "vehicle_history")
public class VehicleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", referencedColumnName = "deviceID", nullable = false)
    private Vehicle vehicle;

////18/09/2024 10am joins for report  
//
//  @ManyToOne(fetch = FetchType.LAZY)
//  @JsonIgnore
//    @JoinColumn(name = "superadmin_id", referencedColumnName = "id", nullable = false)  // Foreign key reference to SuperAdmin
//    private SuperAdmin superAdmin;
// 
//  @ManyToOne(fetch = FetchType.EAGER)
//  private Admin admin;


    @Column(name = "timestamp")
    private Timestamp timestamp;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private String course;
    private String additionalData;
    private Integer sequenceNumber;

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
    
    public Long getId() {
        return id;
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

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
  //Bidirectional relationship with ViolationReport
@OneToMany(mappedBy = "vehicleHistory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<ViolationReport> violationReports;

public List<ViolationReport> getViolationReports() {
	return violationReports;
}

public void setViolationReports(List<ViolationReport> violationReports) {
	this.violationReports = violationReports;
}


//panic code 17/10/2024

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
}
