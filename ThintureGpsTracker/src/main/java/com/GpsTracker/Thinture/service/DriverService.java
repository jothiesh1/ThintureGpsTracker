package com.GpsTracker.Thinture.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.model.Driver;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.ClientRepository;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.DriverRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.repository.UserRepository;
import com.GpsTracker.Thinture.security.AuthenticationFacade;

import com.GpsTracker.Thinture.Util.RoleHierarchyUtil;
import com.GpsTracker.Thinture.dto.DriverDTO;
import com.GpsTracker.Thinture.dto.RoleHierarchyDTO;
import com.GpsTracker.Thinture.model.Driver;
import com.GpsTracker.Thinture.repository.*;
import com.GpsTracker.Thinture.security.AuthenticationFacade;
import com.GpsTracker.Thinture.service.UserTypeFilterService.UserTypeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import com.GpsTracker.Thinture.service.UserTypeFilterService;
import com.GpsTracker.Thinture.service.UserTypeFilterService.UserTypeResult;

import com.GpsTracker.Thinture.dto.DriverDTO;

@Service
public class DriverService {

	
	private static final Logger logger = LoggerFactory.getLogger(DriverService.class);
	
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private AuthenticationFacade authFacade;

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private UserRepository userRepository;

    
    @Autowired
    private UserTypeFilterService userTypeFilterService;

    public Driver addDriverFromDTO(DriverDTO dto) {
        Driver driver = convertToEntity(dto); // Apply fields from DTO

        // 🔐 Get logged-in user details
        String email = authFacade.getAuthentication().getName();
        UserTypeFilterService.UserTypeResult creator = userTypeFilterService.findUserAndTypeByEmail(email);

        logger.info("📥 Add Driver by: {} (Role: {})", email, creator.getRole());

        // 📌 Set role hierarchy
        RoleHierarchyDTO hierarchy = RoleHierarchyUtil.prepareHierarchy(creator);

        driver.setSuperadmin_id(hierarchy.getSuperadmin_id());
        driver.setAdmin_id(hierarchy.getAdmin_id());
        driver.setDealer_id(hierarchy.getDealer_id());
        driver.setClient_id(hierarchy.getClient_id());
        driver.setUser_id(hierarchy.getUser_id());

        // 📦 Final log before save
        logger.info("📦 Final Driver Save Payload:");
        logger.info("➡ Name: {}", driver.getFullName());
        logger.info("➡ Superadmin ID: {}", driver.getSuperadmin_id());
        logger.info("➡ Admin ID: {}", driver.getAdmin_id());
        logger.info("➡ Dealer ID: {}", driver.getDealer_id());
        logger.info("➡ Client ID: {}", driver.getClient_id());
        logger.info("➡ User ID: {}", driver.getUser_id());

        Driver saved = driverRepository.save(driver);
        logger.info("✅ Driver saved with ID: {}", saved.getId());

        return saved;
    }


    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Optional<Driver> getDriverById(Long id) {
        return driverRepository.findById(id);
    }

    public Driver updateDriverFromDTO(Long id, DriverDTO dto) {
        return driverRepository.findById(id).map(driver -> {
            updateEntityFromDTO(driver, dto);
            return driverRepository.save(driver);
        }).orElseThrow(() -> new RuntimeException("Driver not found with id " + id));
    }

    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }

    // 🔄 Convert DTO to entity and set role-based ownership
    private Driver convertToEntity(DriverDTO dto) {
        Driver driver = new Driver();
        updateEntityFromDTO(driver, dto);

        String role = authFacade.getAuthenticatedUserRole();
        Long userId = authFacade.getAuthenticatedUserId();

        if (role == null || userId == null) {
            throw new RuntimeException("Unauthorized: Missing user role or ID from context");
        }

        System.out.println("📌 Creating Driver | Role: " + role + " | User ID: " + userId);

        switch (role) {
            case "ROLE_DEALER" -> {
                dealerRepository.findById(userId).ifPresent(driver::setDealer);
                System.out.println("✅ Assigned to Dealer ID: " + userId);
            }
            case "ROLE_ADMIN" -> {
                adminRepository.findById(userId).ifPresent(driver::setAdmin);
                System.out.println("✅ Assigned to Admin ID: " + userId);
            }
            case "ROLE_CLIENT" -> {
                clientRepository.findById(userId).ifPresent(driver::setClient);
                System.out.println("✅ Assigned to Client ID: " + userId);
            }
            case "ROLE_SUPERADMIN" -> {
                driver.setSuperAdmin(superAdminRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("SuperAdmin not found")));
                System.out.println("✅ Assigned to SuperAdmin ID: " + userId);
            }
            case "ROLE_USER" -> {
                driver.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found")));
                System.out.println("✅ Assigned to User ID: " + userId);
            }
            default -> throw new RuntimeException("Unauthorized role: " + role);
        }

        return driver;
    }

    private void updateEntityFromDTO(Driver driver, DriverDTO dto) {
        driver.setFullName(dto.getFullName());
        driver.setDob(dto.getDob());
        driver.setContact(dto.getContact());
        driver.setEmail(dto.getEmail());
        driver.setAddress(dto.getAddress());
        driver.setLicense(dto.getLicense());
        driver.setLicenseType(dto.getLicenseType());
        driver.setDdp(dto.getDdp());
        driver.setDdpExpiry(dto.getDdpExpiry());
        driver.setVehicle(dto.getVehicle());
        driver.setRfid(dto.getRfid());
        driver.setCountry(dto.getCountry());
    }
    
 // 🔄 Toggle driver active/locked status
    public String toggleDriverStatus(Long id) {
        Optional<Driver> optional = driverRepository.findById(id);
        if (optional.isPresent()) {
            Driver driver = optional.get();
            driver.setStatus(!driver.isStatus());
            driverRepository.save(driver);
            return driver.isStatus() ? "Active" : "Locked";
        } else {
            throw new RuntimeException("Driver not found with id: " + id);
        }
    }

} 
