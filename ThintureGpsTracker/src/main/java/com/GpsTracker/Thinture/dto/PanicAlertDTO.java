package com.GpsTracker.Thinture.dto;

import java.sql.Timestamp;

    
	public class PanicAlertDTO {
	    private Long id;
	    private String vehicleId;
	    private Double latitude;
	    private Double longitude;
	    private Timestamp timestamp;
	    private Integer panic;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getVehicleId() {
			return vehicleId;
		}
		public void setVehicleId(String vehicleId) {
			this.vehicleId = vehicleId;
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
		public Integer getPanic() {
			return panic;
		}
		public void setPanic(Integer panic) {
			this.panic = panic;
		}

	    // Constructors, Getters, and Setters
	}
