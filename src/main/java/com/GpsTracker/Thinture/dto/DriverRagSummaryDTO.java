package com.GpsTracker.Thinture.dto;

import java.util.List;

public class DriverRagSummaryDTO {
    private List<DriverRagReportDTO> reportList;
    private double maxSpeed;
    private double totalDistance;

    // Getters and setters
    public List<DriverRagReportDTO> getReportList() {
        return reportList;
    }
    public void setReportList(List<DriverRagReportDTO> reportList) {
        this.reportList = reportList;
    }
    public double getMaxSpeed() {
        return maxSpeed;
    }
    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
    public double getTotalDistance() {
        return totalDistance;
    }
    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }
}