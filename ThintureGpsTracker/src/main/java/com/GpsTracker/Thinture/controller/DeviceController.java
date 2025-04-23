package com.GpsTracker.Thinture.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.model.Device;
import com.GpsTracker.Thinture.model.Driver;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.service.DeviceService;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vehicles")
public class DeviceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/add-device")
    public ResponseEntity<String> addDevice(@RequestBody Device device) {
        logger.info("Received request to add device: {}", device);
        try {
            deviceService.saveDevice(device);
            logger.info("Device information added successfully!");
            return ResponseEntity.ok("Device information added successfully!");
        } catch (Exception e) {
            logger.error("Error occurred while adding device information", e);
            return ResponseEntity.status(500).body("Error adding device information");
        }
    }

}
//@Controller
//@RequestMapping("/devices")
//public class DeviceController {
//
//    @Autowired
//    private DeviceService deviceService;
//
//    @GetMapping
//    public String listDevices(Model model) {
//        model.addAttribute("devices", deviceService.getAllDevices());
//        return "device/list";
//    }
//
//    @GetMapping("/{id}")
//    public String getDevice(@PathVariable Long id, Model model) {
//        model.addAttribute("device", deviceService.getDeviceById(id));
//        return "device/details";
//    }
//
//    @PostMapping
//    public String saveDevice(@ModelAttribute Device device) {
//        deviceService.saveDevice(device);
//        return "redirect:/devices";
//    }
//
//    @DeleteMapping("/{id}")
//    public String deleteDevice(@PathVariable Long id) {
//        deviceService.deleteDevice(id);
//        return "redirect:/devices";
//    }
//}
