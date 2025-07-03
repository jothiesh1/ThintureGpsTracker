package com.GpsTracker.Thinture.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.GpsTracker.Thinture.dto.VehicleDTO;
import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.model.User;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.repository.ClientRepository;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.UserRepository;
import com.GpsTracker.Thinture.repository.VehicleHistoryRepository;
import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import com.GpsTracker.Thinture.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** ࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏
 * Author: Jothiesh ☃☃☃☃☃☃☃
 * Senior Developer, R&D 
 *
 * ⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂
 * 
 */


@Service
public class VehicleService {
    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);
    
    private final VehicleRepository vehicleRepository;
    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Autowired
    private VehicleLastLocationRepository vehicleLastLocationRepository;
    
    
    @Autowired
    private VehicleHistoryRepository vehicleHistoryRepository ;
    
   
    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private UserRepository userRepository ;


//    // Save or update a vehicle
//    public Vehicle save(Vehicle vehicle) {
//        return vehicleRepository.save(vehicle);
//    }

    // Retrieve all vehicles
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    // Retrieve a vehicle by ID
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    // Count all vehicles
    public long countAllVehicles() {
        return vehicleRepository.count();
    }

    // Find a vehicle by device ID
    public Optional<Vehicle> getVehicleByDeviceID(String deviceID) {
        return vehicleRepository.findByDeviceID(deviceID);
    }

    // Retrieve the last known locations of all vehicles
    public List<VehicleLastLocation> getLastKnownLocations() {
        return vehicleLastLocationRepository.findAll();
    }

    
    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }
    
    
    public List<VehicleLastLocation> getAllLastKnownLocations() {
        return vehicleLastLocationRepository.findAll();
    }

    public Optional<VehicleLastLocation> getLastKnownLocationByDeviceId(String deviceId) {
        return vehicleLastLocationRepository.findByDeviceId(deviceId);
    }

//    public void save(Vehicle vehicle) {
//        logger.info("Attempting to save vehicle: {}", vehicle);
//
//        // Ensure that the deviceID is null if it is an empty string
//        if (vehicle.getDeviceID() != null && vehicle.getDeviceID().isEmpty()) {
//            vehicle.setDeviceID(null);
//        }
//
//        try {
//            vehicleRepository.save(vehicle);
//            logger.info("Vehicle saved successfully: {}", vehicle);
//        } catch (Exception e) {
//            logger.error("Error occurred while saving vehicle: {}", e.getMessage());
//            throw e;
//        }
//    }

    public Vehicle findByVehicleNumber(String vehicleNumber) {
        logger.info("Finding vehicle by number: {}", vehicleNumber);
        return vehicleRepository.findByVehicleNumber(vehicleNumber)
                .orElse(null);
    }

    public void save(Vehicle vehicle) {
        logger.info("Saving vehicle: {}", vehicle);
        vehicleRepository.save(vehicle);
        logger.info("Vehicle saved successfully: {}", vehicle);
    }
    
    
    
    // create device html code
    
    public Vehicle addDeviceInformation(Date installationDate, String deviceID, String technicianName,
            String imei, String simNumber, String dealerName,
            String addressPhone, String country) {
logger.info("Adding new device information.");
Vehicle vehicle = new Vehicle();
vehicle.setInstallationDate(installationDate);
vehicle.setDeviceID(deviceID);
vehicle.setTechnicianName(technicianName);
vehicle.setImei(imei);
vehicle.setSimNumber(simNumber);
vehicle.setDealerName(dealerName);
vehicle.setAddressPhone(addressPhone);
vehicle.setCountry(country);

logger.info("Device information added successfully.");
return vehicleRepository.save(vehicle);
}
 
    
    
    //code for report dropdown
    public List<String> getAllDeviceIDs() {
        return vehicleRepository.findAllDistinctDeviceIDs();
    }
    
   
    
    
    /**
     * Add a single vehicle to Vehicle and VehicleHistory tables.
     *
     * @param serialNo  the serial number of the vehicle
     * @param imei      the IMEI of the vehicle's device
     * @param dealerId  the ID of the dealer
     * @param dealerName the name of the dealer
    */
 // ✅ Add Single Vehicle with Dealer ID
    public void addVehicle(String serialNo, String imei, Long dealerId) {
        try {
            logger.info("📌 Adding Vehicle: Serial No = {}, IMEI = {}, Dealer ID = {}", serialNo, imei, dealerId);

            // ✅ Check if Serial Number Already Exists
            if (vehicleRepository.findBySerialNo(serialNo).isPresent()) {
                throw new IllegalArgumentException("❌ Serial No already exists: " + serialNo);
            }

            // ✅ Fetch Dealer by ID
            Dealer dealer = dealerRepository.findById(dealerId)
                    .orElseThrow(() -> new IllegalArgumentException("❌ Dealer ID not found: " + dealerId));

            // ✅ Create Vehicle
            Vehicle vehicle = new Vehicle();
            vehicle.setSerialNo(serialNo);
            vehicle.setImei(imei);
            vehicle.setDealer(dealer);  // Owner

            // ✅ Get Logged-in User (Admin/SuperAdmin) and assign to Vehicle
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                Long userId = userDetails.getId();
                String role = userDetails.getAuthorities().stream().findFirst().get().getAuthority();

                if ("ROLE_ADMIN".equals(role)) {
                    Admin admin = new Admin();
                    admin.setId(userId);
                    vehicle.setAdmin(admin);  // sets admin_id in DB
                    logger.info("👤 Vehicle created by Admin ID: {}", userId);
                } else if ("ROLE_SUPERADMIN".equals(role)) {
                    SuperAdmin superAdmin = new SuperAdmin();
                    superAdmin.setId(userId);
                    vehicle.setSuperAdmin(superAdmin);  // sets superadmin_id in DB
                    logger.info("👤 Vehicle created by SuperAdmin ID: {}", userId);
                }
            }

            // ✅ Save to Vehicle table
            vehicleRepository.save(vehicle);
            logger.info("✅ Vehicle Saved: Serial No = {}, Dealer ID = {}", serialNo, dealerId);

            // ✅ Save to VehicleHistory Table
            VehicleHistory history = new VehicleHistory();
            history.setSerialNo(serialNo);
            history.setImei(imei);
         //   history.setDealer(dealer);
            history.setVehicle(vehicle);
            vehicleHistoryRepository.save(history);
            logger.info("✅ Vehicle History Saved: Serial No = {}", serialNo);

        } catch (Exception e) {
            logger.error("❌ Error adding vehicle: {}", serialNo, e);
            throw e;
        }
    }


    // ✅ Add Multiple Vehicles in Range
    /**
     * ✅ Add Multiple Vehicles in a Dual Serial Range
     */
    public void addDualVehicles(List<String> serialNumbers, Long dealerId) {
        try {
            logger.info("📌 Adding Dual Vehicles for Dealer ID: {}", dealerId);

            // ✅ Fetch Dealer by ID
            Dealer dealer = dealerRepository.findById(dealerId)
                    .orElseThrow(() -> new IllegalArgumentException("❌ Dealer ID not found: " + dealerId));

            // ✅ Get logged-in user info once (outside the loop)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = null;
            String role = null;

            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                userId = userDetails.getId();
                role = userDetails.getAuthorities().stream().findFirst().get().getAuthority();
            }

            // ✅ Loop through each Serial Number & Save Vehicle
            for (String serialNo : serialNumbers) {
                if (vehicleRepository.findBySerialNo(serialNo).isPresent()) {
                    logger.warn("⚠️ Serial No already exists, skipping: {}", serialNo);
                    continue; // Skip duplicates
                }

                Vehicle vehicle = new Vehicle();
                vehicle.setSerialNo(serialNo);
                vehicle.setImei(""); // Default IMEI for dual serials
                vehicle.setDealer(dealer); // Owner

                // ✅ Set Admin or SuperAdmin ID if available
                if ("ROLE_ADMIN".equals(role)) {
                    Admin admin = new Admin();
                    admin.setId(userId);
                    vehicle.setAdmin(admin);
                } else if ("ROLE_SUPERADMIN".equals(role)) {
                    SuperAdmin superAdmin = new SuperAdmin();
                    superAdmin.setId(userId);
                    vehicle.setSuperAdmin(superAdmin);
                }

                vehicleRepository.save(vehicle);
                logger.info("✅ Added vehicle with Serial No: {}", serialNo);
            }

        } catch (Exception e) {
            logger.error("❌ Error adding dual vehicles:", e);
            throw e;
        }
    }


 
   
    @Transactional
    public void saveVehicleAndDevice(Vehicle vehicleDetails) {
        logger.info("🔄 [START] Saving or updating vehicle information.");

        if (vehicleDetails == null) {
            logger.error("❌ Received NULL vehicleDetails! Exiting method.");
            return;
        }

        String incomingSerialNo = vehicleDetails.getSerialNo();
        if (incomingSerialNo == null || incomingSerialNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Serial number is required.");
        }

        Vehicle existingVehicle = vehicleRepository.findBySerialNo(incomingSerialNo).orElse(null);

        // Check if "NA" is being used for placeholder
        if (existingVehicle == null && incomingSerialNo.equalsIgnoreCase("NA")) {
            logger.info("🔍 Checking for existing 'NA' entry...");
            existingVehicle = vehicleRepository.findBySerialNo("NA").orElse(null);
        }

        if (existingVehicle == null) {
            logger.info("🆕 Creating new vehicle entry...");
            existingVehicle = new Vehicle();
        } else {
            logger.info("✏️ Updating existing vehicle entry...");
        }

        // Only overwrite serialNo if it's not "NA"
        if (!"NA".equalsIgnoreCase(incomingSerialNo)) {
            existingVehicle.setSerialNo(incomingSerialNo);
        }

        // Update user if provided
        if (vehicleDetails.getUser() != null && vehicleDetails.getUser().getId() != null) {
            userRepository.findById(vehicleDetails.getUser().getId()).ifPresent(existingVehicle::setUser);
        }

        // Update remaining fields (excluding deviceID)
        existingVehicle.setVehicleNumber(vehicleDetails.getVehicleNumber());
        existingVehicle.setOwnerName(vehicleDetails.getOwnerName());
        existingVehicle.setVehicleType(vehicleDetails.getVehicleType());
        existingVehicle.setEngineNumber(vehicleDetails.getEngineNumber());
        existingVehicle.setManufacturer(vehicleDetails.getManufacturer());
        existingVehicle.setModel(vehicleDetails.getModel());
        existingVehicle.setInstallationDate(vehicleDetails.getInstallationDate());
        existingVehicle.setTechnicianName(vehicleDetails.getTechnicianName());
        existingVehicle.setSimNumber(vehicleDetails.getSimNumber());
        existingVehicle.setDealerName(vehicleDetails.getDealerName());
        existingVehicle.setAddressPhone(vehicleDetails.getAddressPhone());
        existingVehicle.setCountry(vehicleDetails.getCountry());
        existingVehicle.setImei(vehicleDetails.getImei()); // ✅ Now using IMEI

        vehicleRepository.save(existingVehicle);
        logger.info("✅ [SUCCESS] Vehicle saved: {}", existingVehicle);
    }

    
    /**
     * Add a vehicle for a client (without `deviceId`).
     */
    public void addClientVehicle(String serialNo, String imei, Long clientId) {
        try {
            logger.info("🚀 Adding vehicle: Serial No = {}, IMEI = {}, Client ID = {}", serialNo, imei, clientId);

            // ✅ Check if Serial Number Exists
            if (vehicleRepository.findBySerialNo(serialNo).isPresent()) {
                throw new IllegalArgumentException("❌ Serial No already exists: " + serialNo);
            }

            // ✅ Fetch Client by ID
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("❌ Client ID not found: " + clientId));

            // ✅ Save to Vehicle Table
            Vehicle vehicle = new Vehicle();
            vehicle.setSerialNo(serialNo);
            vehicle.setImei(imei);
            vehicle.setClient(client); // ✅ Associate Client Object
            vehicleRepository.save(vehicle);
            logger.info("✅ Saved vehicle: Serial No = {} for Client ID: {}", serialNo, clientId);

            // ✅ Save to VehicleHistory Table
            VehicleHistory history = new VehicleHistory();
            history.setSerialNo(serialNo);
            history.setImei(imei);
          //  history.setClient(client); // ✅ Associate Client Object in History
            history.setVehicle(vehicle);
            vehicleHistoryRepository.save(history);
            logger.info("✅ Saved vehicle history for: Serial No = {}", serialNo);

        } catch (Exception e) {
            logger.error("❌ Error adding vehicle: Serial No = {}, IMEI = {}", serialNo, imei, e);
            throw e;
        }
    }
    public void addDualClientVehicles(List<String> serialNumbers, Long clientId) {
        try {
            logger.info("📌 Adding Dual Vehicles for Client ID: {}", clientId);

            // ✅ Fetch Client by ID
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("❌ Client ID not found: " + clientId));

            // ✅ Loop through each Serial Number & Save
            for (String serialNo : serialNumbers) {
                if (vehicleRepository.findBySerialNo(serialNo).isPresent()) {
                    logger.warn("⚠️ Skipping existing Serial No: {}", serialNo);
                    continue;
                }

                Vehicle vehicle = new Vehicle();
                vehicle.setSerialNo(serialNo);
                vehicle.setImei(""); // Default IMEI for dual range
                vehicle.setClient(client); // ✅ Associate Client
                vehicleRepository.save(vehicle);

                // ✅ Save to VehicleHistory
                VehicleHistory history = new VehicleHistory();
                history.setSerialNo(serialNo);
                history.setImei("");
             //   history.setClient(client);
                history.setVehicle(vehicle);
                vehicleHistoryRepository.save(history);

                logger.info("✅ Added vehicle with Serial No: {} for Client ID: {}", serialNo, clientId);
            }

        } catch (Exception e) {
            logger.error("❌ Error adding dual vehicles for Client ID: {}", clientId, e);
            throw e;
        }
    }


  
 // ✅ Fetch All Vehicles and Convert to DTO List
    @Transactional(readOnly = true)
    public List<VehicleDTO> getAllVehicless() {
        logger.info("📡 Fetching all vehicles (excluding those with null deviceId or installationDate)...");

        List<VehicleDTO> vehicles = vehicleRepository.findAll().stream()
                .filter(vehicle -> vehicle.getDeviceID() != null && vehicle.getInstallationDate() != null)
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        logger.info("✅ Retrieved {} vehicles.", vehicles.size());
        return vehicles;
    }


    // ✅ Fetch Single Vehicle By deviceID with Logger
    @Transactional(readOnly = true)
    public Optional<VehicleDTO> getVehicleByDeviceID1(String deviceID) {
        logger.info("🔍 Searching for vehicle with Device ID: {}", deviceID);
        
        Optional<Vehicle> vehicle = vehicleRepository.findVehicleByDeviceIDNative(deviceID);

        if (vehicle.isPresent()) {
            logger.info("✅ Found Vehicle: {}", vehicle.get());
        } else {
            logger.warn("❌ No vehicle found with Device ID: {}", deviceID);
        }

        return vehicle.map(this::convertToDTO);
    }

    // ✅ Convert Vehicle Entity to DTO with Logger
    private VehicleDTO convertToDTO(Vehicle vehicle) {
        logger.debug("🔄 Converting Vehicle entity to DTO for Device ID: {}", vehicle.getDeviceID());
        return new VehicleDTO(
        	    vehicle.getDeviceID(),
        	    vehicle.getOwnerName(),
        	    vehicle.getImei(),
        	    vehicle.getSimNumber(),
        	    vehicle.getVehicleNumber(),
        	    vehicle.getEngineNumber(),
        	    vehicle.getModel(),
        	    vehicle.getVehicleType()
        	);

               
        
    }
    

    
 
    
    public List<String> getSerialNosStartingWith(String query) {
        return vehicleRepository.findSerialNosByQuery(query);
    }

    public Optional<Vehicle> getVehicleBySerialNo(String serialNo) {
        return vehicleRepository.findBySerialNo(serialNo);
    }
    
    
    public List<Vehicle> getSerialNosStartingWithh(String query) {
        return vehicleRepository.findVehiclesBySerialQuery(query);
    }


    // ✅ Get All Serial Numbers
    public List<String> getAllSerialNumbers() {
        return vehicleRepository.findAllSerialNumbers();
    }

    // ✅ Update Client for a Vehicle
    @Transactional
    public boolean updateClientForVehicle(String serialNo, Long clientId) {
        Optional<Vehicle> vehicleOptional = vehicleRepository.findBySerialNo(serialNo);
        Optional<Client> clientOptional = clientRepository.findById(clientId);

        if (vehicleOptional.isPresent() && clientOptional.isPresent()) {
            Vehicle vehicle = vehicleOptional.get();
            vehicle.setClient(clientOptional.get());
            vehicleRepository.save(vehicle);
            return true;
        } else {
            return false;
        }
    }

    public void deleteVehicleByDeviceID(String deviceID) {
        Optional<Vehicle> vehicle = vehicleRepository.findByDeviceID(deviceID);
        if (vehicle.isPresent()) {
            vehicleRepository.deleteByDeviceID(deviceID);
            System.out.println("Vehicle deleted: " + deviceID);
        } else {
            throw new RuntimeException("Vehicle with ID " + deviceID + " not found.");
        }
    }



    
    
 // Update an existing vehicle
    public Vehicle updateVehicle(String deviceID, Vehicle updatedVehicle) {
        return vehicleRepository.findByDeviceID(deviceID).map(vehicle -> {
            vehicle.setVehicleNumber(updatedVehicle.getVehicleNumber());
            vehicle.setOwnerName(updatedVehicle.getOwnerName());
            vehicle.setImei(updatedVehicle.getImei());
            vehicle.setEngineNumber(updatedVehicle.getEngineNumber());
            vehicle.setVehicleType(updatedVehicle.getVehicleType());
            vehicle.setModel(updatedVehicle.getModel());
            return vehicleRepository.save(vehicle);
        }).orElse(null);
    }


}
    
    

    
    
