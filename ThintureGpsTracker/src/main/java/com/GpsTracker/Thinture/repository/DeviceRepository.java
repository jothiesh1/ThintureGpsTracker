package com.GpsTracker.Thinture.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GpsTracker.Thinture.model.Device;

public interface DeviceRepository extends JpaRepository<Device, Long>{

}
