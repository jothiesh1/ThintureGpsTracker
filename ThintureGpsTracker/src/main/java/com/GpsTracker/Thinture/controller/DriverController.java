package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.Driver;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.service.DriverService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/drivers")
public class DriverController {

    private static final Logger logger = LoggerFactory.getLogger(DriverController.class);

    @Autowired
    private DriverService driverService;
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @GetMapping("/add")
    public String showAddDriverForm() {
        logger.info("Displaying the Add Driver Form");
        return "add_driver"; // Make sure this matches the HTML file name exactly
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addDriver(@RequestBody Driver driver) {
        Map<String, String> response = new HashMap<>();
        logger.info("Received request to add driver: {}", driver);
        try {
            // Authentication logic here
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
                String currentUsername = authentication.getName();
                logger.info("Current logged-in user: {}", currentUsername);

                // Check user role and assign Admin/SuperAdmin
                if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                    Admin admin = adminRepository.findByEmailIgnoreCase(currentUsername);
                    if (admin != null) {
                        driver.setAdmin(admin);
                        logger.info("Admin ID {} set for the driver.", admin.getId());
                    } else {
                        response.put("error", "Admin not found");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPERADMIN"))) {
                    SuperAdmin superAdmin = superAdminRepository.findByEmail(currentUsername);
                    if (superAdmin != null) {
                        driver.setSuperAdmin(superAdmin);
                        logger.info("SuperAdmin ID {} set for the driver.", superAdmin.getId());
                    } else {
                        response.put("error", "SuperAdmin not found");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                } else {
                    response.put("error", "Unauthorized user");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }

                // Save the driver
                driverService.saveDriver(driver);
                logger.info("Driver added successfully: {}", driver);
                response.put("message", "Driver added successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "No authenticated user found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (ConstraintViolationException e) {
            StringBuilder validationErrors = new StringBuilder("Validation failed: ");
            e.getConstraintViolations().forEach(violation -> {
                validationErrors.append(violation.getPropertyPath()).append(": ")
                        .append(violation.getMessage()).append("; ");
            });
            response.put("error", validationErrors.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Error occurred while saving driver: ", e);
            response.put("error", "Error saving driver");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


 // Method to display dealer details view
    @GetMapping("/view")
    public String showDriverDetails(Model model) {
        logger.info("Received request to view driver details.");

        try {
            // Fetch the list of drivers from the service
            List<Driver> drivers = driverService.getAllDrivers();
            model.addAttribute("drivers", drivers); // Add drivers to the model
            logger.info("Fetched {} drivers from the database.", drivers.size());
        } catch (Exception e) {
            logger.error("Error occurred while fetching driver details: ", e);
        }

        return "view_drivers"; // Ensure this matches the file name "view_drivers.html"
    }


    @GetMapping
    public ResponseEntity<List<Driver>> getAllDrivers() {
        logger.info("Received request to fetch all drivers");

        try {
            // Fetch all drivers from the service
            List<Driver> drivers = driverService.getAllDrivers();

            // If no drivers found, return 204 No Content
            if (drivers.isEmpty()) {
                logger.warn("No drivers found");
                return ResponseEntity.noContent().build();
            }

            // Log the number of drivers found and return them with status 200 OK
            logger.info("Fetched {} drivers", drivers.size());
            return ResponseEntity.ok(drivers);

        } catch (Exception e) {
            // Log the error and return 500 Internal Server Error
            logger.error("Error fetching drivers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PutMapping("/edit/{id}")
    public ResponseEntity<String> editDriver(@PathVariable Long id, @RequestBody Driver driver) {
        logger.info("Received request to edit driver with ID: {}", id);
        try {
            driverService.updateDriver(id, driver);
            logger.info("Driver with ID: {} updated successfully", id);
            return ResponseEntity.ok("Driver updated successfully");
        } catch (EntityNotFoundException e) {
            logger.warn("Driver with ID: {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found");
        } catch (Exception e) {
            logger.error("Error updating driver with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating driver");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable Long id) {
        logger.info("Received request to delete driver with ID: {}", id);
        try {
            driverService.deleteDriver(id);
            logger.info("Driver with ID: {} deleted successfully", id);
            return ResponseEntity.ok("Driver deleted successfully");
        } catch (EntityNotFoundException e) {
            logger.warn("Driver with ID: {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found");
        } catch (Exception e) {
            logger.error("Error deleting driver with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting driver");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Driver>> searchDriverByName(@RequestParam String driverName) {
        logger.info("Received request to search drivers by name: {}", driverName);
        try {
            List<Driver> drivers = driverService.searchDriverByName(driverName);
            logger.info("Found {} drivers with name: {}", drivers.size(), driverName);
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            logger.error("Error searching drivers by name: {}", driverName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

