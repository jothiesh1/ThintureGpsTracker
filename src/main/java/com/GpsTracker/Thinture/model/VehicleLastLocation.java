package com.GpsTracker.Thinture.model;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Table(name = "vehicle_last_location")
public class VehicleLastLocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, unique = true)
    private String deviceId;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;
    @Column(name = "status", nullable = false)
    private String status;  // N1, N2, etc.
    
    @Column(name = "admin_id")
    private Long admin_id;

    @Column(name = "dealer_id")
    private Long dealer_id;

    @Column(name = "client_id")
    private Long client_id;

    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "superadmin_id")
    private Long superadmin_id;

    @Column(name = "driver_id")
    private Long driver_id; // optional if not used

    private String ignition ;
    
    private String VehicleStatus ;
    
    
    private String speed ;
      
    @Column(name = "serial_no")
    private String serialNo;

    @Column(name = "imei")
    private String imei;

    // Add getters and setters
    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

  
 public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
public Admin getAdmin() {
		return admin;
	}
	public void setAdmin(Admin admin) {
		this.admin = admin;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	// Relationship with Admin
   
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
	@JsonIgnore
	private User user;

    public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Dealer getDealer() {
		return dealer;
	}
	public void setDealer(Dealer dealer) {
		this.dealer = dealer;
	}
	public String getVehicleStatus() {
		return VehicleStatus;
	}
	public void setVehicleStatus(String vehicleStatus) {
		VehicleStatus = vehicleStatus;
	}
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	private String course;
	public String getIgnition() {
		return ignition;
	}
	public void setIgnition(String ignition) {
		this.ignition = ignition;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	
	@Override public Long getAdmin_id() { return admin_id; }
	@Override public void setAdmin_id(Long admin_id) { this.admin_id = admin_id; }

	@Override public Long getDealer_id() { return dealer_id; }
	@Override public void setDealer_id(Long dealer_id) { this.dealer_id = dealer_id; }

	@Override public Long getClient_id() { return client_id; }
	@Override public void setClient_id(Long client_id) { this.client_id = client_id; }

	@Override public Long getUser_id() { return user_id; }
	@Override public void setUser_id(Long user_id) { this.user_id = user_id; }

	@Override public Long getSuperadmin_id() { return superadmin_id; }
	@Override public void setSuperadmin_id(Long superadmin_id) { this.superadmin_id = superadmin_id; }

	@Override public Long getDriver_id() { return driver_id; }
	@Override public void setDriver_id(Long driver_id) { this.driver_id = driver_id; }

	public String getImei() {
	    return imei;
	}

	public void setImei(String imei) {
	    this.imei = imei;
	}

	
}
