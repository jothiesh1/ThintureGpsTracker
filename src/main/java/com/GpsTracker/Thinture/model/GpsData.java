package com.GpsTracker.Thinture.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "gps_data")
public class GpsData implements Serializable {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id; // This field serves as the primary key
    private String deviceID;
    private String timestamp;
    private String dataValidity;
    private String status;
    private String latitude;
    private String longitude;
    private String speed;
    private String course;
    private String additionalData;
    private String sequenceNumber;
    private String ignition; // New Field
	private String vehicleStatus ;
	
	private String timeIntervals ;
	
	private String distanceItervals ;
	
	
	private String gsmStrength;
    // Getters and Setters

	// :CODE IMEI
	  @JsonProperty("IMEI")  
	private String imei;  // ✅ Add this field

	public String getImei() {
	    return imei;
	}

	public void setImei(String imei) {
	    this.imei = imei;
	}




	public String getGsmStrength() {
		return gsmStrength;
	}



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



	public void setGsmStrength(String gsmStrength) {
		this.gsmStrength = gsmStrength;
	}



	public String getDeviceID() {
        return deviceID;
    }



	public String getVehicleStatus() {
		return vehicleStatus;
	}



	public void setVehicleStatus(String vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}



	public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDataValidity() {
        return dataValidity;
    }

    public void setDataValidity(String dataValidity) {
        this.dataValidity = dataValidity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
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

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    // Convert latitude and longitude to double
    public double getLatitudeAsDouble() {
        return convertCoordinate(latitude);
    }

    public double getLongitudeAsDouble() {
        return convertCoordinate(longitude);
    }

    private double convertCoordinate(String coordinate) {
        if (coordinate == null || coordinate.isEmpty()) {
            throw new IllegalArgumentException("Invalid coordinate");
        }
        String value = coordinate.substring(0, coordinate.length() - 1);
        return Double.parseDouble(value);
    }
 // Getters and Setters
    public String getIgnition() {
        return ignition;
    }

    public void setIgnition(String ignition) {
        this.ignition = ignition;
    }
    @Override
    public String toString() {
        return "GpsData{" +
                "DeviceID='" + deviceID + '\'' +
                "imei='" + imei + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", DataValidity='" + dataValidity + '\'' +
                ", status='" + status + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", speed='" + speed + '\'' +
                ", course='" + course + '\'' +
                ", speed='" + speed + '\'' +
               ", additionalData='" + additionalData +'\''+
                ", ignition='" + ignition +'\''+
                 ", vehicleStatus='" + vehicleStatus +'\''+
                '}';
    }
}
