package com.GpsTracker.Thinture.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.model.Driver;
import com.GpsTracker.Thinture.repository.DriverRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {

    private static final Logger logger = LoggerFactory.getLogger(DriverService.class);

    @Autowired
    private DriverRepository driverRepository;

    /**
     * Save a new Driver into the database.
     * 
     * @param driver The Driver entity to save.
     */
    public void saveDriver(Driver driver) {
        logger.info("Attempting to save a new driver: {}", driver);
        try {
            driverRepository.save(driver);
            logger.info("Successfully saved driver: {}", driver.getFirstName() + " " + driver.getFamilyName());
        } catch (Exception e) {
            logger.error("Error occurred while saving driver: {}", driver, e);
        }
    }

    /**
     * Retrieve all drivers from the database.
     * 
     * @return List of all drivers.
     */
    public List<Driver> getAllDrivers() {
        logger.info("Fetching all drivers from the database.");
        List<Driver> drivers = new ArrayList<>();
        try {
            drivers = driverRepository.findAll();
            logger.info("Successfully fetched {} drivers.", drivers.size());
        } catch (Exception e) {
            logger.error("Error occurred while fetching drivers", e);
        }
        return drivers;
    }

    

    public void updateDriver(Long driverId, Driver updatedDriver) throws EntityNotFoundException {
        logger.info("Updating driver with ID: {}", driverId);
        Driver existingDriver = driverRepository.findById(driverId)
                .orElseThrow(() -> {
                    logger.warn("Driver with ID: {} not found", driverId);
                    return new EntityNotFoundException("Driver not found");
                });

        // Update driver fields
        existingDriver.setFirstName(updatedDriver.getFirstName());
        existingDriver.setSecondName(updatedDriver.getSecondName());
        existingDriver.setFamilyName(updatedDriver.getFamilyName());
        existingDriver.setEmployeeNumber(updatedDriver.getEmployeeNumber());
        existingDriver.setPhone(updatedDriver.getPhone());
        existingDriver.setDepartment(updatedDriver.getDepartment());
        existingDriver.setCountry(updatedDriver.getCountry());
        existingDriver.setLicenseNumber(updatedDriver.getLicenseNumber());
        existingDriver.setDefensivePermit(updatedDriver.getDefensivePermit());
        existingDriver.setPermitExpiry(updatedDriver.getPermitExpiry());
        existingDriver.setManagerName(updatedDriver.getManagerName());
        existingDriver.setManagerEmail(updatedDriver.getManagerEmail());
        existingDriver.setFocalPointName(updatedDriver.getFocalPointName());
        existingDriver.setFocalPointEmail(updatedDriver.getFocalPointEmail());

        driverRepository.save(existingDriver);
        logger.info("Driver with ID: {} updated successfully", driverId);
    }

    public void deleteDriver(Long driverId) throws EntityNotFoundException {
        logger.info("Deleting driver with ID: {}", driverId);
        if (!driverRepository.existsById(driverId)) {
            logger.warn("Driver with ID: {} not found", driverId);
            throw new EntityNotFoundException("Driver not found");
        }
        driverRepository.deleteById(driverId);
        logger.info("Driver with ID: {} deleted successfully", driverId);
    }

    public List<Driver> searchDriverByName(String firstName) {
        logger.info("Searching drivers by name: {}", firstName);
        return driverRepository.findByFirstNameContainingOrFamilyNameContaining(firstName, firstName);
    }

    }

