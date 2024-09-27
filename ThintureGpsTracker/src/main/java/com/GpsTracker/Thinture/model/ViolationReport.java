package com.GpsTracker.Thinture.model;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;

import java.util.ArrayList;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "violation_report")
public class ViolationReport {

    private static final Logger logger = LoggerFactory.getLogger(ViolationReport.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship with Vehicle entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", referencedColumnName = "deviceID", nullable = false)
    private Vehicle vehicle;

    // Relationship with VehicleHistory entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", referencedColumnName = "id", nullable = false)
    private VehicleHistory vehicleHistory;

    @Column(name = "violation_date")
    private Date violationDate;

    @Column(name = "violation_type")
    private String violationType;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status;

    // Getters and Setters with logging

    public Long getId() {
        logger.debug("Getting ID: {}", id);
        return id;
    }

    public void setId(Long id) {
        logger.debug("Setting ID: {}", id);
        this.id = id;
    }

    public Vehicle getVehicle() {
        logger.debug("Getting Vehicle: {}", vehicle);
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        logger.debug("Setting Vehicle: {}", vehicle);
        this.vehicle = vehicle;
    }

    public VehicleHistory getVehicleHistory() {
        logger.debug("Getting Vehicle History: {}", vehicleHistory);
        return vehicleHistory;
    }

    public void setVehicleHistory(VehicleHistory vehicleHistory) {
        logger.debug("Setting Vehicle History: {}", vehicleHistory);
        this.vehicleHistory = vehicleHistory;
    }

    public Date getViolationDate() {
        logger.debug("Getting Violation Date: {}", violationDate);
        return violationDate;
    }

    public void setViolationDate(Date violationDate) {
        logger.debug("Setting Violation Date: {}", violationDate);
        this.violationDate = violationDate;
    }

    public String getViolationType() {
        logger.debug("Getting Violation Type: {}", violationType);
        return violationType;
    }

    public void setViolationType(String violationType) {
        logger.debug("Setting Violation Type: {}", violationType);
        this.violationType = violationType;
    }

    public Double getSpeed() {
        logger.debug("Getting Speed: {}", speed);
        return speed;
    }

    public void setSpeed(Double speed) {
        logger.debug("Setting Speed: {}", speed);
        this.speed = speed;
    }

    public String getLocation() {
        logger.debug("Getting Location: {}", location);
        return location;
    }

    public void setLocation(String location) {
        logger.debug("Setting Location: {}", location);
        this.location = location;
    }

    public String getStatus() {
        logger.debug("Getting Status: {}", status);
        return status;
    }

    public void setStatus(String status) {
        logger.debug("Setting Status: {}", status);
        this.status = status;
    }
}
