package com.GpsTracker.Thinture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GpsTracker.Thinture.model.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    // Corrected method to search drivers by first name or family name
    List<Driver> findByFirstNameContainingOrFamilyNameContaining(String firstName, String familyName);
}


	

