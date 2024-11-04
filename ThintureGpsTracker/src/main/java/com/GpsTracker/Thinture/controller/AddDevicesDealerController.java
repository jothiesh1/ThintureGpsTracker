package com.GpsTracker.Thinture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.service.AdminService;
import com.GpsTracker.Thinture.service.DealerService;
@Controller
@RequestMapping("/dealer")
public class AddDevicesDealerController {

    private static final Logger logger = LoggerFactory.getLogger(AddDevicesDealerController.class);

    @Autowired
    private DealerService dealerService;

    @GetMapping("/addDevices")
    public String showAddDevicesPage(Model model) {
        // Add necessary data to the model if needed
        model.addAttribute("dealers", dealerService.getAllDealers());
        return "add_devices_dealer";
    }

    @PostMapping("/addSingleSerial")
    public String addSingleSerial(@RequestParam("dealerNameSingle") String dealerName,
                                  @RequestParam("singleSerial") String serialNumber,
                                  Model model) {
        // Validate dealer name and serial number
        if (dealerName.isEmpty() || serialNumber.isEmpty()) {
            model.addAttribute("error", "Dealer name and serial number are required.");
            return "add_devices_dealer";
        }

        try {
            dealerService.addSingleSerial(dealerName, serialNumber); // Custom service to handle serial addition
            model.addAttribute("message", "Serial number added successfully.");
        } catch (Exception e) {
            logger.error("Error adding single serial number", e);
            model.addAttribute("error", "Failed to add the serial number.");
        }

        return "redirect:/dealer/addDevices"; // Redirect to avoid form resubmission
    }

    @PostMapping("/addDualSerials")
    public String addDualSerials(@RequestParam("dealerNameDual") String dealerName,
                                 @RequestParam("startSerial") int startSerial,
                                 @RequestParam("endSerial") int endSerial,
                                 @RequestParam("removedSerial") int removedSerial,
                                 Model model) {
        if (dealerName.isEmpty() || startSerial > endSerial) {
            model.addAttribute("error", "Invalid serial number range or dealer name.");
            return "add_devices_dealer";
        }

        try {
            dealerService.addDualSerials(dealerName, startSerial, endSerial, removedSerial); // Custom service
            model.addAttribute("message", "Serial numbers added successfully.");
        } catch (Exception e) {
            logger.error("Error adding dual serial numbers", e);
            model.addAttribute("error", "Failed to add serial numbers.");
        }

        return "redirect:/dealer/addDevices";
    }
}
