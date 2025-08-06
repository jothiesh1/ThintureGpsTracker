package com.GpsTracker.Thinture.mapper;



//installation contrller and installation .html all user

import com.GpsTracker.Thinture.dto.VehicleDTO;
import com.GpsTracker.Thinture.model.Vehicle;

public class VehicleMapper {
    public static Vehicle toEntity(VehicleDTO dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setDeviceID(dto.getDeviceID());
        vehicle.setSerialNo(dto.getSerialNo());
        vehicle.setOwnerName(dto.getOwnerName());
        vehicle.setImei(dto.getImei());
        vehicle.setSimNumber(dto.getSimNumber());
        vehicle.setVehicleNumber(dto.getVehicleNumber());
        vehicle.setEngineNumber(dto.getEngineNumber());
        vehicle.setVehicleType(dto.getVehicleType());
        vehicle.setModel(dto.getModel());
        vehicle.setInstallationDate(dto.getInstallationDate());
        vehicle.setRenewalDate(dto.getRenewalDate());
        vehicle.setTechnicianName(dto.getTechnicianName());
        vehicle.setDealerName(dto.getDealerName());
        vehicle.setAddressPhone(dto.getAddressPhone());
        vehicle.setCountry(dto.getCountry());
        vehicle.setUser_id(dto.getUser_id());
        vehicle.setDriver_id(dto.getDriverId());
        return vehicle;
    }
}
