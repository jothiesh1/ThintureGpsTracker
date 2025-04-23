package com.GpsTracker.Thinture.dto;

public class DistanceKmCalcDTO {

    private String deviceImei;
    private String deviceId;
    private double todayKm;
    private double totalKm;
    private String timeSlot;
    private double mileage;

    // Used for chart summary
    private String label;
    private double km;

    public DistanceKmCalcDTO() {}

    // 📊 For chart summary use only
    public DistanceKmCalcDTO(String label, double km) {
        this.label = label;
        this.km = km;
    }

    // ✅ Full Getters/Setters

    public String getDeviceImei() { return deviceImei; }
    public void setDeviceImei(String deviceImei) { this.deviceImei = deviceImei; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public double getTodayKm() { return todayKm; }
    public void setTodayKm(double todayKm) { this.todayKm = todayKm; }

    public double getTotalKm() { return totalKm; }
    public void setTotalKm(double totalKm) { this.totalKm = totalKm; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public double getMileage() { return mileage; }
    public void setMileage(double mileage) { this.mileage = mileage; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public double getKm() { return km; }
    public void setKm(double km) { this.km = km; }
}
