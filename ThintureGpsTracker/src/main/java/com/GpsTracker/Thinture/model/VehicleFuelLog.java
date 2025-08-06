package com.GpsTracker.Thinture.model;


import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vehicle_fuel_log")
public class VehicleFuelLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private LocalDate date;

    // 🔹 Fuel added at this instance (in liters)
    @Column(nullable = false)
    private double fuelFilled;

    // 🔸 Optional field for storing fuel left after trip
    private Double fuelRemaining;

    // 🔧 Optional field to tag who added the fuel (user, driver, etc.)
    private String addedBy;

    // 🔧 Optional remarks (fuel station, notes, etc.)
    private String remarks;

    // Getters and Setters

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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getFuelFilled() {
        return fuelFilled;
    }

    public void setFuelFilled(double fuelFilled) {
        this.fuelFilled = fuelFilled;
    }

    public Double getFuelRemaining() {
        return fuelRemaining;
    }

    public void setFuelRemaining(Double fuelRemaining) {
        this.fuelRemaining = fuelRemaining;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
