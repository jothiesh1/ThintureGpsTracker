package com.GpsTracker.Thinture.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.GpsTracker.Thinture.dto.VehicleDTO;
import com.GpsTracker.Thinture.dto.VehicleRenewalDTO;
import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.User;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.repository.ClientRepository;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.DriverRepository;
import com.GpsTracker.Thinture.repository.UserRepository;
import com.GpsTracker.Thinture.repository.VehicleHistoryRepository;
import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private VehicleLastLocationRepository vehicleLastLocationRepository;
    
    
    @Autowired
    private VehicleHistoryRepository vehicleHistoryRepository ;
    
   
    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private UserRepository userRepository ;
    private DriverRepository driverRepository;
    @Autowired
    public VehicleService(
        VehicleRepository vehicleRepository,
        DriverRepository driverRepository,
        UserRepository userRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
    }

    
    /**
     * 📊 Get renewal summary for dashboard pie chart
     */
    public List<Map<String, Object>> getMonthlyInstallationRenewalReport() {
        logger.info("📅 Fetching monthly installation and renewal summary report...");

        // ⬇️ Fetch installation counts
        List<Object[]> installations = vehicleRepository.fetchInstallationsGroupedByMonth();
        logger.info("📦 Installations fetched: {}", installations.size());

        // ⬇️ Fetch renewal counts
        List<Object[]> renewals = vehicleRepository.fetchRenewalsGroupedByMonth();
        logger.info("🔁 Renewals fetched: {}", renewals.size());

        // ⬇️ Convert to Map for merging
        Map<String, Long> installMap = installations.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));

        Map<String, Long> renewMap = renewals.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));

        logger.debug("🧮 Installation Map: {}", installMap);
        logger.debug("🧮 Renewal Map: {}", renewMap);

        // ⬇️ Merge all unique months
        Set<String> allMonths = new HashSet<>();
        allMonths.addAll(installMap.keySet());
        allMonths.addAll(renewMap.keySet());

        logger.info("🗓️ Merging data for total {} months", allMonths.size());

        List<Map<String, Object>> finalReport = allMonths.stream().map(month -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("month", month);
            entry.put("installations", installMap.getOrDefault(month, 0L));
            entry.put("renewals", renewMap.getOrDefault(month, 0L));
            return entry;
        }).sorted(Comparator.comparing(e -> (String) e.get("month")))
          .collect(Collectors.toList());

        logger.info("✅ Final monthly chart data prepared with {} records", finalReport.size());
        return finalReport;
    }

    
    
    
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
logger.info("🛠 Adding new device information for deviceID: {}", deviceID);

Vehicle vehicle = new Vehicle();
vehicle.setInstallationDate(installationDate);
vehicle.setDeviceID(deviceID);
vehicle.setTechnicianName(technicianName);
vehicle.setImei(imei);
vehicle.setSimNumber(simNumber);
vehicle.setDealerName(dealerName);
vehicle.setAddressPhone(addressPhone);
vehicle.setCountry(country);

// ✅ Auto set renewalDate = installationDate + 1 year
if (installationDate != null) {
LocalDate renewal = installationDate.toLocalDate().plusYears(1);
vehicle.setRenewalDate(Date.valueOf(renewal));
logger.info("📅 Renewal date set to: {}", renewal);
} else {
logger.warn("⚠️ Installation date is null. Renewal date cannot be set.");
}

// ✅ Ensure renewed flag is false
vehicle.setRenewed(false);
logger.info("🔁 Renewal status initialized to: false");

Vehicle saved = vehicleRepository.save(vehicle);
logger.info("✅ Device information saved successfully: {}", saved.getId());

return saved;
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
    
    

    //not use still api direct  AddDevicesDealerController
    public void addVehicle(String serialNo, String imei, Long dealerId) {
        try {
            logger.info("📌 Adding Vehicle: Serial No = {}, IMEI = {}, Dealer ID = {}", serialNo, imei, dealerId);

            // ✅ Check if Serial Number Already Exists
            List<Vehicle> existingVehicles = vehicleRepository.findBySerialNo(serialNo);
            if (!existingVehicles.isEmpty()) {
                throw new IllegalArgumentException("❌ Serial No already exists: " + serialNo);
            }

            // ✅ Fetch Dealer by ID
            Dealer dealer = dealerRepository.findById(dealerId)
                    .orElseThrow(() -> new IllegalArgumentException("❌ Dealer ID not found: " + dealerId));

            // ✅ Save Vehicle with Dealer ID
            Vehicle vehicle = new Vehicle();
            vehicle.setSerialNo(serialNo);
            vehicle.setImei(imei);
            vehicle.setDealer(dealer);  // ✅ Set Dealer Reference

            vehicleRepository.save(vehicle);
            logger.info("✅ Vehicle Saved: Serial No = {}, Dealer ID = {}", serialNo, dealerId);

            // ✅ Save to VehicleHistory Table
            VehicleHistory history = new VehicleHistory();
            history.setSerialNo(serialNo);
            history.setImei(imei);
            history.setDealer(dealer);  // ✅ Store Dealer ID
            history.setVehicle(vehicle);

            vehicleHistoryRepository.save(history);
            logger.info("✅ Vehicle History Saved: Serial No = {}", serialNo);

            // ✅ Save VehicleLastLocation WITH fallback deviceId (using SerialNo)
            VehicleLastLocation lastLocation = new VehicleLastLocation();

            // 👉 Fallback: Use serialNo as deviceId (until the real device is assigned later)
            String deviceId = vehicle.getDeviceID();
            if (deviceId == null || deviceId.trim().isEmpty()) {
                deviceId = serialNo;
                logger.warn("⚠️ No deviceID found; using SerialNo '{}' as temporary deviceId.", serialNo);
            }

            lastLocation.setDeviceId(deviceId); // Mandatory field (NOT NULL in DB)
            lastLocation.setSerialNo(serialNo);
            lastLocation.setImei(imei);
            lastLocation.setLatitude(0.0);
            lastLocation.setLongitude(0.0);
            lastLocation.setStatus("INIT");
            lastLocation.setTimestamp(new Timestamp(System.currentTimeMillis()));

            // ✅ Set role IDs
            lastLocation.setDealer_id(vehicle.getDealer_id());
            lastLocation.setAdmin_id(vehicle.getAdmin_id());
            lastLocation.setClient_id(vehicle.getClient_id());
            lastLocation.setUser_id(vehicle.getUser_id());
            lastLocation.setSuperadmin_id(vehicle.getSuperadmin_id());
            lastLocation.setDriver_id(vehicle.getDriver_id());

            logger.info("✅ Prepared VehicleLastLocation: SerialNo={}, IMEI={}, DeviceID={}, DealerID={}, AdminID={}, ClientID={}, UserID={}, SuperAdminID={}",
                    lastLocation.getSerialNo(),
                    lastLocation.getImei(),
                    lastLocation.getDeviceId(),
                    lastLocation.getDealer_id(),
                    lastLocation.getAdmin_id(),
                    lastLocation.getClient_id(),
                    lastLocation.getUser_id(),
                    lastLocation.getSuperadmin_id()
            );

            vehicleLastLocationRepository.save(lastLocation);
            logger.info("✅ Vehicle Last Location Saved: Serial No = {}", serialNo);

        } catch (Exception e) {
            logger.error("❌ Error adding vehicle: {}", serialNo, e);
            throw e;
        }
    }


    
    
    
    
    
    // ✅ Add Multiple Vehicles in Range
    /**
     * ✅ Add Multiple Vehicles in a Dual Serial Range
     */
    
    //not use still api direct AddDevicesDealerController
    public void addDualVehicles(List<String> serialNumbers, Long dealerId) {
        try {
            logger.info("📌 Adding Dual Vehicles for Dealer ID: {}", dealerId);

            // ✅ Fetch Dealer by ID
            Dealer dealer = dealerRepository.findById(dealerId)
                    .orElseThrow(() -> new IllegalArgumentException("❌ Dealer ID not found: " + dealerId));

            // ✅ Loop through each Serial Number & Save Vehicle
            for (String serialNo : serialNumbers) {
                List<Vehicle> existingVehicles = vehicleRepository.findBySerialNo(serialNo);
                if (!existingVehicles.isEmpty()) {
                    logger.warn("⚠️ Serial No already exists, skipping: {}", serialNo);
                    continue; // Skip duplicates
                }

                Vehicle vehicle = new Vehicle();
                vehicle.setSerialNo(serialNo);
                vehicle.setImei(""); // Default IMEI for dual serials
                vehicle.setDealer(dealer); // ✅ Assign Dealer to Vehicle
                vehicleRepository.save(vehicle);

                // ✅ Save to VehicleHistory Table
                VehicleHistory history = new VehicleHistory();
                history.setSerialNo(serialNo);
                history.setImei("");
                history.setDealer(dealer);  // ✅ Store Dealer ID
                history.setVehicle(vehicle);
                vehicleHistoryRepository.save(history);

                // ✅ Save to VehicleLastLocation (NEW)
                VehicleLastLocation lastLocation = new VehicleLastLocation();
                lastLocation.setDeviceId(vehicle.getDeviceID()); // If available
                lastLocation.setSerialNo(serialNo);
                lastLocation.setImei("");  // Empty IMEI for dual serials
                lastLocation.setLatitude(0.0);
                lastLocation.setLongitude(0.0);
                lastLocation.setStatus("INIT");
                lastLocation.setTimestamp(new Timestamp(System.currentTimeMillis()));
                vehicleLastLocationRepository.save(lastLocation);

                logger.info("✅ Added vehicle + history + last location for Serial No: {} (Dealer ID: {})", serialNo, dealerId);
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
            throw new IllegalArgumentException("❌ Vehicle details cannot be null.");
        }

        String serialNo = vehicleDetails.getSerialNo();
        if (serialNo == null || serialNo.trim().isEmpty()) {
            throw new IllegalArgumentException("❌ Serial number is required.");
        }

        // ✅ Fetch vehicle by serialNo and installationDate = null
        List<Vehicle> vehicles = vehicleRepository.findBySerialNo(serialNo);
        Vehicle existingVehicle = null;

        for (Vehicle v : vehicles) {
            if (v.getInstallationDate() == null) {
                existingVehicle = v;
                break;
            }
        }

        if (existingVehicle == null) {
            logger.error("🚫 No vehicle found with serialNo '{}' and null installationDate", serialNo);
            throw new IllegalArgumentException("No available vehicle with this serial number for installation.");
        }

        // ❌ Check for IMEI duplication
        String incomingImei = vehicleDetails.getImei();
        if (incomingImei != null && !incomingImei.trim().isEmpty()) {
            Optional<Vehicle> duplicateImeiVehicle = vehicleRepository.findByImei(incomingImei);
            if (duplicateImeiVehicle.isPresent() &&
                !duplicateImeiVehicle.get().getSerialNo().equalsIgnoreCase(serialNo)) {
                logger.error("❌ IMEI '{}' is already assigned to serialNo '{}'",
                        incomingImei, duplicateImeiVehicle.get().getSerialNo());
                throw new IllegalArgumentException("IMEI is already in use by another vehicle.");
            }
            existingVehicle.setImei(incomingImei);
        }

        // ✅ Set remaining fields
        Long userId = (vehicleDetails.getUser() != null) ? vehicleDetails.getUser().getId() : null;
        Long driverId = vehicleDetails.getDriver_id();

        if (userId != null) {
            existingVehicle.setUser_id(userId);
            logger.info("👤 Set user_id: {}", userId);
        }

        if (driverId != null) {
            if (!driverRepository.existsById(driverId)) {
                throw new IllegalArgumentException("Invalid driver ID: " + driverId);
            }
            existingVehicle.setDriver_id(driverId);
        }

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

        if (vehicleDetails.getInstallationDate() != null && existingVehicle.getRenewalDate() == null) {
            existingVehicle.setRenewalDate(Date.valueOf(vehicleDetails.getInstallationDate().toLocalDate().plusYears(1)));
        }

        if (existingVehicle.getRenewed() == null) {
            existingVehicle.setRenewed(false);
        }

        // 💾 Save vehicle
        vehicleRepository.save(existingVehicle);

        // 🔄 Update vehicle_last_location
        if (incomingImei != null && !incomingImei.trim().isEmpty()) {
            Optional<VehicleLastLocation> locOpt = vehicleLastLocationRepository.findByImei(incomingImei);
            if (locOpt.isPresent()) {
                VehicleLastLocation loc = locOpt.get();
                if (userId != null) loc.setUser_id(userId);
                if (driverId != null) loc.setDriver_id(driverId);
                vehicleLastLocationRepository.save(loc);
                logger.info("📍 Updated vehicle_last_location for IMEI {}", incomingImei);
            } else {
                logger.warn("⚠️ No vehicle_last_location found for IMEI {}", incomingImei);
            }
        }

        logger.info("✅ Vehicle installation data saved successfully for serialNo: {}", serialNo);
    }

    
    
    
    
    
    
    
    
    /**
     * Add a vehicle for a client (without `deviceId`).
     */
    public void addClientVehicle(String serialNo, String imei, Long clientId) {
        try {
            logger.info("🚀 Adding vehicle: Serial No = {}, IMEI = {}, Client ID = {}", serialNo, imei, clientId);

            if (!vehicleRepository.findBySerialNo(serialNo).isEmpty()) {
                throw new IllegalArgumentException("❌ Serial No already exists: " + serialNo);
            }

            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("❌ Client ID not found: " + clientId));

            // ✅ Save Vehicle
            Vehicle vehicle = new Vehicle();
            vehicle.setSerialNo(serialNo.trim());
            vehicle.setImei(imei.trim());
            vehicle.setClient_id(client.getId());         // ✅ MUST store client_id
            vehicle.setClient(client);                    // ✅ optional, but safe for hybrid mapping
            vehicleRepository.save(vehicle);
            logger.info("✅ Vehicle saved: Serial No = {} (Client ID: {})", serialNo, clientId);

            // ✅ Save VehicleHistory
            VehicleHistory history = new VehicleHistory();
            history.setSerialNo(serialNo.trim());
            history.setImei(imei.trim());
          //  history.setClient_id(client.getId());         // ✅ store client_id (MUST)
            history.setClient(client);                    // ✅ hybrid mapping safe
            history.setVehicle(vehicle);
            vehicleHistoryRepository.save(history);
            logger.info("✅ Vehicle history saved for Serial No: {}", serialNo);

            // ✅ Save VehicleLastLocation
            VehicleLastLocation lastLocation = new VehicleLastLocation();
            lastLocation.setSerialNo(serialNo.trim());
            lastLocation.setImei(imei.trim());
            lastLocation.setLatitude(0.0);
            lastLocation.setLongitude(0.0);
            lastLocation.setStatus("INIT");
            lastLocation.setTimestamp(new Timestamp(System.currentTimeMillis()));
            lastLocation.setClient_id(client.getId());    // ✅ store client_id
            lastLocation.setClient(client);               // ✅ hybrid mapping safe
            vehicleLastLocationRepository.save(lastLocation);
            logger.info("✅ Vehicle last location saved for Serial No: {}", serialNo);

        } catch (Exception e) {
            logger.error("❌ Error adding vehicle: Serial No = {}, IMEI = {}", serialNo, imei, e);
            throw e;
        }
    }


    public void addDualClientVehicles(List<Map<String, Object>> devices, Long clientId) {
        try {
            logger.info("📌 Adding multiple vehicles for Client ID: {}", clientId);

            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new IllegalArgumentException("❌ Client ID not found: " + clientId));

            for (Map<String, Object> deviceMap : devices) {
                String serialNo = (String) deviceMap.get("serialNo");
                String imei = (String) deviceMap.get("imei");

                if (serialNo == null || serialNo.isBlank() || imei == null || imei.isBlank()) {
                    logger.warn("⚠️ Skipping incomplete device: {}", deviceMap);
                    continue;
                }

                if (!vehicleRepository.findBySerialNo(serialNo).isEmpty()) {
                    logger.warn("⚠️ Skipping existing Serial No: {}", serialNo);
                    continue;
                }

                // ✅ Save Vehicle
                Vehicle vehicle = new Vehicle();
                vehicle.setSerialNo(serialNo.trim());
                vehicle.setImei(imei.trim());
                vehicle.setClient_id(client.getId());     // ✅ store client_id
                vehicle.setClient(client);                // ✅ hybrid mapping safe
                vehicleRepository.save(vehicle);

                // ✅ Save VehicleHistory
                VehicleHistory history = new VehicleHistory();
                history.setSerialNo(serialNo.trim());
                history.setImei(imei.trim());
              //  history.setClient_id(client.getId());     // ✅ store client_id (this was missing)
                history.setClient(client);                // ✅ hybrid mapping safe
                history.setVehicle(vehicle);
                vehicleHistoryRepository.save(history);

                // ✅ Save VehicleLastLocation
                VehicleLastLocation lastLocation = new VehicleLastLocation();
                lastLocation.setSerialNo(serialNo.trim());
                lastLocation.setImei(imei.trim());
                lastLocation.setLatitude(0.0);
                lastLocation.setLongitude(0.0);
                lastLocation.setStatus("INIT");
                lastLocation.setTimestamp(new Timestamp(System.currentTimeMillis()));
                lastLocation.setClient_id(client.getId()); // ✅ store client_id
                lastLocation.setClient(client);            // ✅ hybrid mapping safe
                vehicleLastLocationRepository.save(lastLocation);

                logger.info("✅ Added vehicle + history + last location for Serial No: {} (Client ID: {})", serialNo, clientId);
            }

        } catch (Exception e) {
            logger.error("❌ Error adding multiple vehicles for Client ID: {}", clientId, e);
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
        	    vehicle.getVehicleType(),
        	    vehicle.getInstallationDate(),
        	    vehicle.getRenewalDate() ,
        	    vehicle.getSerialNo()
        	);

               
        
    }
    

    
 
    
    public List<String> getSerialNosStartingWith(String query) {
        logger.info("🔍 Searching for serial numbers containing '{}'", query);

        List<String> serialNos = vehicleRepository.findSerialNosByQuery(query);

        if (serialNos.isEmpty()) {
            logger.warn("❌ No serial numbers found matching '{}' with NULL installationDate", query);
        } else {
            logger.info("✅ Found {} matching serial numbers for query '{}': {}", serialNos.size(), query, serialNos);
        }

        return serialNos;
    }

    
    
    public Optional<Vehicle> getVehicleBySerialNo(String serialNo) {
        List<Vehicle> vehicles = vehicleRepository.findBySerialNo(serialNo);

        if (vehicles.isEmpty()) {
            logger.warn("❌ No vehicle found with serialNo '{}'", serialNo);
            return Optional.empty();
        }

        // Filter for vehicle with null installationDate
        for (Vehicle v : vehicles) {
            if (v.getInstallationDate() == null) {
                logger.info("✅ Vehicle with serialNo '{}' found with null installationDate", serialNo);
                return Optional.of(v);
            }
        }

        logger.warn("🚫 All vehicles with serialNo '{}' already have installationDate set", serialNo);
        return Optional.empty();
    }



    
    
    public List<Vehicle> getSerialNosStartingWithh(String query) {
        return vehicleRepository.findVehiclesBySerialQuery(query);
    }


    // ✅ Get All Serial Numbers
    public List<String> getAllSerialNumbers() {
        return vehicleRepository.findAllSerialNumbers();
    }

    // ✅ Update Client for a Vehicle html code is add_device_to_client.html
    @Transactional
    public boolean updateClientForVehicle(String serialNo, Long clientId) {
        List<Vehicle> vehicles = vehicleRepository.findBySerialNo(serialNo);
        Optional<Client> clientOptional = clientRepository.findById(clientId);

        if (!vehicles.isEmpty() && clientOptional.isPresent()) {
            for (Vehicle vehicle : vehicles) {
                vehicle.setClient_id(clientId);
                vehicleRepository.save(vehicle);
                logger.info("✅ Updated vehicle with serialNo {} to clientId {}", serialNo, clientId);

                // 🔄 Also update vehicle_last_location with clientId (based on IMEI)
                String imei = vehicle.getImei();
                if (imei != null && !imei.trim().isEmpty()) {
                    Optional<VehicleLastLocation> locOpt = vehicleLastLocationRepository.findByImei(imei);
                    if (locOpt.isPresent()) {
                        VehicleLastLocation loc = locOpt.get();
                        loc.setClient_id(clientId);
                        vehicleLastLocationRepository.save(loc);
                        logger.info("📍 Updated vehicle_last_location for IMEI {} with clientId {}", imei, clientId);
                    } else {
                        logger.warn("⚠️ No vehicle_last_location found for IMEI {}", imei);
                    }
                } else {
                    logger.warn("⚠️ Vehicle with serialNo {} has no IMEI set", serialNo);
                }
            }
            return true;
        } else {
            logger.warn("❌ No vehicle(s) or client found for update (serialNo: {}, clientId: {})", serialNo, clientId);
            return false;
        }
    }





    @Transactional
    public void deleteVehicleByDeviceID(String deviceID) {
        logger.info("🗑️ Attempting to delete Vehicle with deviceID={}", deviceID);

        Vehicle vehicle = vehicleRepository.findByDeviceID(deviceID)
            .orElseThrow(() -> new RuntimeException("❌ Vehicle with ID " + deviceID + " not found."));

        vehicleRepository.delete(vehicle);
        logger.info("✅ Vehicle deleted: {}", deviceID);
    }

    @Transactional
    public void deleteLastLocationByDeviceID(String deviceID) {
        logger.info("🗑️ Attempting to delete VehicleLastLocation with deviceID={}", deviceID);

        vehicleLastLocationRepository.findByDeviceId(deviceID)
            .ifPresentOrElse(
                location -> {
                    vehicleLastLocationRepository.delete(location);
                    logger.info("✅ Deleted VehicleLastLocation with deviceID={}", deviceID);
                },
                () -> logger.warn("⚠️ No VehicleLastLocation found for deviceID={}", deviceID)
            );
    }

    @Transactional
    public void deleteVehicleAndLastLocation(String deviceID) {
        logger.info("🗑️ DELETE request for both Vehicle AND LastLocation with deviceID={}", deviceID);
        deleteVehicleByDeviceID(deviceID);
        deleteLastLocationByDeviceID(deviceID);
        logger.info("✅ Finished deleting Vehicle and VehicleLastLocation with deviceID={}", deviceID);
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
    
    

    /**
     * 🔍 Fetch vehicles by renewal filter type
     * @param days -1 = expired, 0 = all (including renewed), > 0 = due in N days
     */
    public List<Vehicle> getRenewalsByDays(int days) {
        LocalDate today = LocalDate.now();
        LocalDate target = today.plusDays(days);

        if (days == -1) {
            logger.info("🔴 Fetching expired vehicles (before {})", today);
            return vehicleRepository.findExpiredUnrenewed(Date.valueOf(today));
        } else if (days == 0) {
            logger.info("🟢 Fetching ALL vehicles with renewalDate set");
            return vehicleRepository.findAllWithRenewalDateRegardlessOfRenewedStatus();
        } else {
            logger.info("🟡 Fetching unrenewed vehicles with renewalDate between {} and {}", today, target);
            return vehicleRepository.findUnrenewedVehiclesInRange(
                    Date.valueOf(today), Date.valueOf(target));
        }
    }

    /**
     * 🧾 Converts entity list to frontend DTO format
     */
    public List<VehicleRenewalDTO> getRenewalDTOsByDays(int days) {
        return getRenewalsByDays(days).stream().map(v -> {
            VehicleRenewalDTO dto = new VehicleRenewalDTO();
            dto.setId(v.getId());
            dto.setDeviceID(v.getDeviceID());
            dto.setVehicleNumber(v.getVehicleNumber());
            dto.setDriverName(v.getTechnicianName() != null ? v.getTechnicianName() : "-"); // Or use v.getDriver().getName()
            dto.setInstallationDate(v.getInstallationDate());
            dto.setRenewalDate(v.getRenewalDate());
            dto.setRenewed(v.getRenewed() != null ? v.getRenewed() : false);
            dto.setRenewalRemarks(v.getRenewalRemarks());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * ✅ Called when user clicks "Renew Now"
     */
    public void markAsRenewed(Long vehicleId, String remarks) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));

        LocalDate now = LocalDate.now();
        logger.info("🛠 Renewing vehicle {} (DeviceID: {})", vehicle.getVehicleNumber(), vehicle.getDeviceID());

        vehicle.setRenewalRemarks("Renewed on " + now + " - " + remarks);
        vehicle.setInstallationDate(Date.valueOf(now));
        vehicle.setRenewalDate(Date.valueOf(now.plusYears(1)));
        vehicle.setRenewed(false); // Reset for next cycle

        vehicleRepository.save(vehicle);

        logger.info("✅ Vehicle {} successfully renewed. Next due on {}", vehicle.getVehicleNumber(), vehicle.getRenewalDate());
    }

    /**
     * 📆 For default legacy loading: upcoming unrenewed vehicles due in N days
     */
    public List<Vehicle> getDueRenewals(int daysAhead) {
        LocalDate targetDate = LocalDate.now().plusDays(daysAhead);
        logger.info("📆 Fetching unrenewed vehicles due before {}", targetDate);
        return vehicleRepository.findExpiredUnrenewed(Date.valueOf(targetDate));
    }
    
 
    
    public void markAsRenewed(Long vehicleId, String remarks, double durationYears) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));

        LocalDate now = LocalDate.now();
        LocalDate renewalDate = now.plusDays((long) (durationYears * 365.25)); // precise calculation

        vehicle.setRenewed(true);
        vehicle.setRenewalDate(Date.valueOf(renewalDate));
        vehicle.setRenewalRemarks("Renewed on " + now + " | Duration: " + durationYears + " years | " + remarks);

        vehicleRepository.save(vehicle);

        logger.info("✅ Vehicle [{} - {}] renewed successfully. New expiry: {}",
                vehicle.getId(), vehicle.getVehicleNumber(), renewalDate);
    }

    
    public Map<String, Long> getVehicleTypeCounts() {
        List<Object[]> result = vehicleRepository.countByVehicleType();

        Map<String, Long> counts = new LinkedHashMap<>();
        for (Object[] row : result) {
            String type = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            counts.put(type, count);
        }
        return counts;
    }

    
  // :CODE IMEI
    //CORE WORK CHANGE DATE 06/05/2025 IMEI
    // ✅ Get vehicle by IMEI (for auto deviceID setting)
    public Optional<Vehicle> getVehicleByImei(String imei) {
        if (imei == null || imei.trim().isEmpty()) {
            logger.warn("❌ IMEI is null or empty.");
            return Optional.empty();
        }
        logger.info("🔍 Fetching vehicle by IMEI: {}", imei);
        return vehicleRepository.findByImei(imei);
    }

    
    //serial no edit
    
    public List<Vehicle> getAllDevices() {
        return vehicleRepository.findAll();
    }

    // Update in both tables
    @Transactional
    public String updateDeviceBothTables(String oldSerial, Map<String, String> payload) {
        try {
            String newSerial = payload.get("serialNo");
            String newImei = payload.get("imei");

            logger.info("🔁 Update requested: oldSerial={}, newSerial={}, newImei={}", oldSerial, newSerial, newImei);

            Optional<Vehicle> vehicleOpt = vehicleRepository.findBySerialNo(oldSerial).stream().findFirst();
            if (vehicleOpt.isEmpty()) {
                logger.warn("🚫 Vehicle not found for serialNo={}", oldSerial);
                return null;
            }

            Vehicle vehicle = vehicleOpt.get();
            String oldImei = vehicle.getImei(); // Save original IMEI before update

            vehicle.setSerialNo(newSerial);
            vehicle.setImei(newImei);
            vehicleRepository.save(vehicle);
            logger.info("✅ Vehicle updated: id={}, serialNo={}, imei={}", vehicle.getId(), newSerial, newImei);

            // Now, only update VehicleLastLocation if serialNo also matches
            Optional<VehicleLastLocation> lastOpt = vehicleLastLocationRepository.findByImeiAndSerialNo(oldImei, oldSerial);
            if (lastOpt.isPresent()) {
                VehicleLastLocation last = lastOpt.get();
                last.setSerialNo(newSerial);
                last.setImei(newImei);
                last.setDeviceId(newSerial);
                vehicleLastLocationRepository.save(last);
                logger.info("✅ VehicleLastLocation updated: id={}, serialNo={}, imei={}", last.getId(), newSerial, newImei);
            } else {
                logger.warn("⚠️ No matching VehicleLastLocation for serialNo={} and imei={}", oldSerial, oldImei);
            }

            return "✅ Updated both vehicle and last location (if matched)";
        } catch (Exception e) {
            logger.error("❌ Exception in updateDeviceBothTables: {}", e.getMessage(), e);
            throw new RuntimeException("Update failed", e);
        }
    }


    // Delete from both tables
    @Transactional
    public String deleteBySerialNo(String serialNo) {
        try {
            List<Vehicle> vehicles = vehicleRepository.findBySerialNo(serialNo);
            if (vehicles.isEmpty()) {
                logger.warn("🚫 No vehicle found for serialNo={}", serialNo);
                return null;
            }

            String imei = vehicles.get(0).getImei(); // Assuming unique serialNo = one record

            Optional<VehicleLastLocation> lastOpt = vehicleLastLocationRepository.findByImeiAndSerialNo(imei, serialNo);

            vehicleRepository.deleteAll(vehicles);
            logger.info("✅ Deleted vehicle(s) with serialNo={}", serialNo);

            if (lastOpt.isPresent()) {
                vehicleLastLocationRepository.delete(lastOpt.get());
                logger.info("✅ Deleted matching VehicleLastLocation for serialNo={} and imei={}", serialNo, imei);
            } else {
                logger.warn("⚠️ No matching VehicleLastLocation for serialNo={} and imei={}", serialNo, imei);
            }

            return "✅ Deleted from both tables (if matched)";
        } catch (Exception e) {
            logger.error("❌ Exception in deleteBySerialNo: {}", e.getMessage(), e);
            throw new RuntimeException("Delete failed", e);
        }
    }
    /**
     * Get comprehensive device expiry analytics
     */
    public Map<String, Object> getDeviceExpiryAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        try {
            // Get all expiry data
            List<Object[]> expiryData = vehicleRepository.findDeviceExpiryCountsByDate();
            
            // Calculate analytics
            Date currentDate = new Date(0, 0, 0);
            Long totalExpired = vehicleRepository.countExpiredDevices(currentDate);
            
            // Group by time periods
            Map<String, Long> monthlyExpiries = groupExpiryByMonth(expiryData);
            Map<String, Long> weeklyExpiries = groupExpiryByWeek(expiryData, currentDate);
            
            analytics.put("totalExpired", totalExpired);
            analytics.put("monthlyExpiries", monthlyExpiries);
            analytics.put("weeklyExpiries", weeklyExpiries);
            analytics.put("expiryTrend", calculateExpiryTrend(expiryData));
            
        } catch (Exception e) {
            logger.error("Error calculating device expiry analytics", e);
            analytics.put("error", "Failed to calculate analytics");
        }
        
        return analytics;
    }
    
    private Map<String, Long> groupExpiryByMonth(List<Object[]> expiryData) {
        Map<String, Long> monthlyData = new LinkedHashMap<>();
        java.text.SimpleDateFormat monthFormat = new java.text.SimpleDateFormat("MMM-yyyy");
        
        for (Object[] row : expiryData) {
            Date expiryDate = (Date) row[0];
            Long count = (Long) row[1];
            String monthKey = monthFormat.format(expiryDate);
            monthlyData.merge(monthKey, count, Long::sum);
        }
        
        return monthlyData;
    }
    
    private Map<String, Long> groupExpiryByWeek(List<Object[]> expiryData, Date currentDate) {
        Map<String, Long> weeklyData = new LinkedHashMap<>();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        
        for (int i = 0; i < 8; i++) { // Next 8 weeks
            Date weekStart = (Date) cal.getTime();
            cal.add(Calendar.DAY_OF_WEEK, 7);
            Date weekEnd = (Date) cal.getTime();
            
            long weekCount = expiryData.stream()
                .filter(row -> {
                    Date expiryDate = (Date) row[0];
                    return expiryDate.after(weekStart) && expiryDate.before(weekEnd);
                })
                .mapToLong(row -> (Long) row[1])
                .sum();
            
            String weekKey = "Week " + (i + 1);
            weeklyData.put(weekKey, weekCount);
        }
        
        return weeklyData;
    }
    
    private String calculateExpiryTrend(List<Object[]> expiryData) {
        if (expiryData.size() < 2) return "STABLE";
        
        // Simple trend calculation based on recent vs older data
        long recentCount = expiryData.stream()
            .skip(Math.max(0, expiryData.size() - 7)) // Last 7 entries
            .mapToLong(row -> (Long) row[1])
            .sum();
            
        long olderCount = expiryData.stream()
            .limit(Math.min(7, expiryData.size()))
            .mapToLong(row -> (Long) row[1])
            .sum();
        
        if (recentCount > olderCount * 1.2) return "INCREASING";
        if (recentCount < olderCount * 0.8) return "DECREASING";
        return "STABLE";
    }
}


    




    
    

    
    
