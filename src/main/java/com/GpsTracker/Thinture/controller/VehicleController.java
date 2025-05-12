package com.GpsTracker.Thinture.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

//import org.hibernate.validator.internal.util.logging.Log_.logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.service.DeviceService;
import com.GpsTracker.Thinture.service.DriverService;
import com.GpsTracker.Thinture.service.MailService;
import com.GpsTracker.Thinture.service.VehicleService;

import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;



@Controller
@RequestMapping("/total_vehicles")
@Slf4j
public class VehicleController {

   private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private MailService mailService;

    
   

    
    
    
    @GetMapping("/search")
    public ResponseEntity<Vehicle> searchVehicle(@RequestParam String vehicleNumber) {
        logger.info("Searching for vehicle with number: {}", vehicleNumber);
        Vehicle vehicle = vehicleService.findByVehicleNumber(vehicleNumber);
        if (vehicle != null) {
            logger.info("Vehicle found: {}", vehicle);
            return ResponseEntity.ok(vehicle);
        } else {
            logger.warn("Vehicle not found for number: {}", vehicleNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/createdevices")
    public ResponseEntity<String> createVehicle(@RequestBody Vehicle vehicle) {
        logger.info("Received request to create vehicle: {}", vehicle);
        try {
            vehicleService.save(vehicle);
            logger.info("Vehicle created successfully: {}", vehicle);
            return ResponseEntity.ok("Vehicle created successfully");
        } catch (ConstraintViolationException e) {
            StringBuilder validationErrors = new StringBuilder("Validation failed: ");
            e.getConstraintViolations().forEach(violation -> {
                validationErrors.append(violation.getPropertyPath()).append(": ")
                                .append(violation.getMessage()).append("; ");
            });
            logger.error(validationErrors.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors.toString());
        } catch (Exception e) {
            logger.error("Error occurred while saving vehicle: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving vehicle");
        }
    }

    @PostMapping("/add-device")
    public ResponseEntity<String> addDeviceInformation(@RequestBody Vehicle deviceDetails) {
        logger.info("Received request to add device information.");
        try {
            vehicleService.addDeviceInformation(
                    deviceDetails.getInstallationDate(),
                    deviceDetails.getDeviceID(),
                    deviceDetails.getTechnicianName(),
                    deviceDetails.getImei(),
                    deviceDetails.getSimNumber(),
                    deviceDetails.getDealerName(),
                    deviceDetails.getAddressPhone(),
                    deviceDetails.getCountry());
            logger.info("Device information added successfully.");
            return ResponseEntity.ok("Device information added successfully");
        } catch (Exception e) {
            logger.error("Error occurred while adding device information: ", e);
            return ResponseEntity.status(500).body("Error adding device information");
        }
    }
    @PostMapping("/add-vehicle-and-device")
    public ResponseEntity<String> addVehicleAndDevice(@RequestBody Vehicle vehicleDetails) {
        logger.info("📩 [Incoming Payload] vehicleDetails: {}", vehicleDetails);

        if (vehicleDetails.getUser() != null) {
            logger.info("🔍 User Object Received: {}", vehicleDetails.getUser());
            logger.info("🆔 Extracted user ID: {}", vehicleDetails.getUser().getId());
        } else {
            logger.warn("⚠️ No user info (null user object) received in request.");
        }

        logger.info("🔍 Extracted driver ID: {}", vehicleDetails.getDriver_id());

        try {
            if (vehicleDetails.getDeviceID() == null || vehicleDetails.getDeviceID().trim().isEmpty()) {
                logger.warn("⚠️ Device ID is missing, assigning 'NA'");
                vehicleDetails.setDeviceID("NA");
            }

            Long driverId = vehicleDetails.getDriver_id();
            if (driverId != null && driverService.getDriverById(driverId).isEmpty()) {
                throw new IllegalArgumentException("Driver ID " + driverId + " does not exist.");
            }

            vehicleService.saveVehicleAndDevice(vehicleDetails);
            logger.info("✅ Vehicle and device saved with driver_id: {}", driverId);

            return ResponseEntity.ok("✅ Vehicle and device information saved successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("🚨 Validation Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("🚨 Internal Error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error: " + e.getMessage());
        }
    }


    
    
    /**
     * Single API to handle both serial number suggestions and fetching vehicle details.
     *
     * @param query (Optional) If provided, fetches serial number suggestions.
     * @param serialNo (Optional) If provided, fetches vehicle details by serial number.
     * @return ResponseEntity containing serial numbers or vehicle details.
     */
    
    @GetMapping
    public ResponseEntity<Object> handleVehicleRequests(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String serialNo) {

        if (query != null) {
            List<String> serialNos = vehicleService.getSerialNosStartingWith(query);
            if (serialNos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(serialNos);
        } else if (serialNo != null) {
            Optional<Vehicle> vehicleOpt = vehicleService.getVehicleBySerialNo(serialNo);
            if (vehicleOpt.isPresent()) {
                Vehicle vehicle = vehicleOpt.get();
                Map<String, String> response = new HashMap<>();
                response.put("imei", vehicle.getImei() != null ? vehicle.getImei() : "NA");
                response.put("technicianName", vehicle.getTechnicianName() != null ? vehicle.getTechnicianName() : "NA");
                response.put("simNumber", vehicle.getSimNumber() != null ? vehicle.getSimNumber() : "NA");
                response.put("dealerName", vehicle.getDealerName() != null ? vehicle.getDealerName() : "NA");
                response.put("vehicleType", vehicle.getVehicleType() != null ? vehicle.getVehicleType() : "NA");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
        return ResponseEntity.badRequest().body("Invalid request. Provide either 'query' or 'serialNo'.");
    }
    
    
    @GetMapping("/api/devices")
    public ResponseEntity<List<Map<String, String>>> getAllVehicles() {
        logger.info("🔍 Fetching all vehicles from the database...");
        
        List<Vehicle> vehicles = vehicleService.getAllDevices();
        logger.info("✅ Retrieved {} vehicle records", vehicles.size());

        List<Map<String, String>> result = new ArrayList<>();
        for (Vehicle v : vehicles) {
            Map<String, String> item = new HashMap<>();
            item.put("serialNo", v.getSerialNo());
            item.put("imei", v.getImei());
            result.add(item);
            logger.debug("📦 Vehicle - Serial: {}, IMEI: {}", v.getSerialNo(), v.getImei());
        }

        return ResponseEntity.ok(result);
    }

    @PutMapping("/api/devicesedit")
    public ResponseEntity<String> updateDeviceBothTables(@RequestParam String serialNo, @RequestBody Map<String, String> payload) {
        logger.info("🛠️ Update request for serialNo: {}", serialNo);
        logger.debug("📥 Payload: {}", payload);

        String result = vehicleService.updateDeviceBothTables(serialNo, payload);
        if (result != null) {
            logger.info("✅ Update successful for serialNo: {}", serialNo);
            return ResponseEntity.ok(result);
        } else {
            logger.warn("❌ Update failed — Device not found: {}", serialNo);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Device not found");
        }
    }

    @DeleteMapping("/api/devicesdelete")
    public ResponseEntity<String> deleteDevice(@RequestParam String serialNo) {
        logger.info("🗑️ Delete request for serialNo: {}", serialNo);

        String result = vehicleService.deleteBySerialNo(serialNo);
        if (result != null) {
            logger.info("✅ Deletion successful for serialNo: {}", serialNo);
            return ResponseEntity.ok(result);
        } else {
            logger.warn("❌ Deletion failed — Device not found: {}", serialNo);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Device not found");
        }
    }


}
    
    // command for 11/09/2024 12pm for 
//    @PostMapping("/createdevices")
//    public ResponseEntity<String> createVehicle(@RequestBody Vehicle vehicle) {
//        try {
//            logger.info("Received request to create vehicle: {}", vehicle);
//            vehicleService.save(vehicle);
//            return ResponseEntity.ok("Vehicle created successfully");
//        } catch (Exception e) {
//            logger.error("Error occurred while saving vehicle: ", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving vehicle");
//        }
//    }

  // code for the visible map in the javascript page
//  @GetMapping("/locations")
//  public List<Vehicle> getGpsLocations() {
//      return vehicleService.getAllVehicles(); // Fetches all vehicles from the database
//  }



//@Controller
//@RequestMapping("/total_vehicles")
//public class VehicleController {
//
//    @Autowired
//    private VehicleService vehicleService;
//
//    @GetMapping
//    public String getVehicles(Model model) {
//        model.addAttribute("vehicles", vehicleService.getAllVehicles());
//        return "total_vehicles";
//    }
//
//    @PostMapping("/add")
//    public String addVehicle(Vehicle vehicle) {
//        vehicleService.addVehicle(vehicle);
//        return "redirect:/total_vehicles";
//    }
//    // code for the visible map in the javascript page
//    @GetMapping("/locations")
//    public List<Vehicle> getGpsLocations() {
//        return vehicleService.getAllVehicles(); // Fetches all vehicles from the database
//    }
//
//    
//    
//    
//    // new code 10/09/2024 jothiesh
//    
//    @GetMapping("/createvehicles")
//    public String showCreateVehiclePage() {
//        return "createvehicles";  // Matches your HTML template
//    }

//  @DeleteMapping("/delete/{deviceID}")
//  public ResponseEntity<Void> deleteVehicle(@PathVariable String deviceID) {
//      vehicleService.deleteVehicle(deviceID);
//      return ResponseEntity.noContent().build();
//  }
    

