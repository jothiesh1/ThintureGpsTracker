package com.GpsTracker.Thinture.dto;


public class LocationUpdate {
  private double latitude;
  private double longitude;
  private String deviceID;
  private String timestamp;

  public LocationUpdate(double latitude, double longitude, String deviceID, String timestamp) {
      this.latitude = latitude;
      this.longitude = longitude;
      this.deviceID = deviceID;
      this.timestamp = timestamp;
  }

  public void setLatitude(double latitude) {
	this.latitude = latitude;
}

public void setLongitude(double longitude) {
	this.longitude = longitude;
}

public void setDeviceID(String deviceID) {
	this.deviceID = deviceID;
}

public void setTimestamp(String timestamp) {
	this.timestamp = timestamp;
}

public double getLatitude() {
      return latitude;
  }

  public double getLongitude() {
      return longitude;
  }

  public String getDeviceID() {
      return deviceID;
  }

  public String getTimestamp() {
      return timestamp;
  }
}
