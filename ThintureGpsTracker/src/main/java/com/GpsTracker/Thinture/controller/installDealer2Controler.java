
package com.GpsTracker.Thinture.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
@RequestMapping("/total_vehicless")
@Slf4j
public class installDealer2Controler {

   private static final Logger logger = LoggerFactory.getLogger(installDealer2Controler.class);

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private DeviceService deviceService;
    //new code vehicle report mail
  //09/10/2024 mail for vehicle report 
    
    @Autowired
    private MailService mailService;
    
    
    
    
    
//    @GetMapping("/createdevices")
//    public String showCreateDevicesPage(Model model) {
//        model.addAttribute("vehicle", new Vehicle()); // Ensure vehicle object is initialized
//        model.addAttribute("driver", new Driver()); // Ensure driver object is initialized
//        model.addAttribute("device", new Device()); // Ensure device object is initialized
//        return "createdevices"; // Return the name of your Thymeleaf template
//    }
//

    
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
    
    //new code vehicle report mail
    //09/10/2024 mail for vehicle report 
     
    }
    
    //new code
    //10/01/2025 ceate device 
    
    @PostMapping("/add-vehicle-and-device")
    public ResponseEntity<String> addVehicleAndDevice(@RequestBody Vehicle vehicleDetails) {
        logger.info("Received request to add or update vehicle and device information: {}", vehicleDetails);

        try {
            // Check for missing serial number or IMEI
            if (vehicleDetails.getSerialNo() == null || vehicleDetails.getImei() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Serial number and IMEI are required.");
            }

            // Save vehicle and device details
            vehicleService.saveVehicleAndDevice(vehicleDetails);

            logger.info("Vehicle and device information processed successfully.");
            return ResponseEntity.ok("Vehicle and device information updated successfully.");

        } catch (IllegalArgumentException e) {
            // Handle specific validation exceptions
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Handle generic exceptions
            logger.error("Error occurred while processing vehicle and device information: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing vehicle and device information.");
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
            logger.info("Fetching serial number suggestions for query: {}", query);
            List<String> serialNos = vehicleService.getSerialNosStartingWith(query);

            if (serialNos.isEmpty()) {
                logger.warn("No serial numbers found for query: {}", query);
                return ResponseEntity.ok(Collections.emptyList()); // ✅ Return empty list instead of 204
            }

            logger.info("Serial numbers found: {}", serialNos);
            return ResponseEntity.ok(serialNos);
        } else if (serialNo != null) {
            logger.info("Fetching vehicle details for Serial No: {}", serialNo);
            Optional<Vehicle> vehicleOpt = vehicleService.getVehicleBySerialNo(serialNo);
            if (vehicleOpt.isPresent()) {
                Vehicle vehicle = vehicleOpt.get();
                Map<String, String> response = new HashMap<>();
                response.put("imei", vehicle.getImei() != null ? vehicle.getImei() : "NA");
                response.put("technicianName", vehicle.getTechnicianName() != null ? vehicle.getTechnicianName() : "NA");
                response.put("simNumber", vehicle.getSimNumber() != null ? vehicle.getSimNumber() : "NA");
                response.put("dealerName", vehicle.getDealerName() != null ? vehicle.getDealerName() : "NA");
                response.put("vehicleType", vehicle.getVehicleType() != null ? vehicle.getVehicleType() : "NA");

                logger.info("Returning vehicle details for Serial No: {}", serialNo);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("No vehicle found for Serial No: {}", serialNo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found");
            }
        }
        
        logger.warn("Invalid request: Provide either 'query' or 'serialNo'");
        return ResponseEntity.badRequest().body("Invalid request. Provide either 'query' or 'serialNo'.");
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
    

