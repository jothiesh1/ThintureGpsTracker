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
