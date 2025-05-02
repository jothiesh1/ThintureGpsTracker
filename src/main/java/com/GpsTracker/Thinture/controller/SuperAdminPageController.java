package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class SuperAdminPageController {

    private static final Logger logger = LoggerFactory.getLogger(SuperAdminPageController.class);

    @GetMapping("/superadmin/add-admin")
    public String addAdminPage(Model model) {
        logger.info("🔹 Accessed SuperAdmin - Add Admin Page");
        return "superadmin/add_admin";
    }
    

	  @GetMapping("/dashboard")
	    public String showAddAdminForm(Model model) {
	        logger.info("Displaying the Add Admin Form");
	       
	        return "superadmin/dashboard"; // HTML form for adding an admin file name is add_admin.html
	    }

    @GetMapping("/superadmin/add-dealer")
    public String addDealerPage(Model model) {
        logger.info("🔹 Accessed SuperAdmin - Add Dealer Page");
        return "superadmin/add_dealer";
    }

    @GetMapping("/superadmin/add-client")
    public String addClientPage(Model model) {
        logger.info("🔹 Accessed SuperAdmin - Add Client Page");
        return "superadmin/add_client";
    }

    @GetMapping("/superadmin/add-user")
    public String addUserPage(Model model) {
        logger.info("🔹 Accessed SuperAdmin - Add User Page");
        return "superadmin/add_user";
    }

    @GetMapping("/superadmin/add-driver")
    public String addDriverPage(Model model) {
        logger.info("🔹 Accessed SuperAdmin - Add Driver Page");
        return "superadmin/add_driver";
    }

    @GetMapping("/superadmin/view-driver")
    public String viewDriverPage(Model model) {
        logger.info("🔹 Accessed SuperAdmin - View Driver Page");
        return "superadmin/view_driver";
    }

    @GetMapping("/superadmin/add-devices-dealer")
    public String addDevicesDealer(Model model) {
        logger.info("🔹 Accessed SuperAdmin - Add Devices to Dealer Page");
        return "superadmin/add_devices_dealer";
    }

    @GetMapping("/superadmin/add-devices-client")
    public String addDevicesClient(Model model) {
        logger.info("🔹 Accessed SuperAdmin - Add Devices to Client Page");
        return "superadmin/add_devices_client";
    }

    @GetMapping("/superadmin/map4")
    public String showMap4(Model model) {
        logger.info("🔹 Accessed SuperAdmin - Map4 Page");
        return "superadmin/map4";
    }

    @GetMapping("/superadmin/playback")
    public String showPlayback(Model model) {
        logger.info("🔹 Accessed SuperAdmin - Playback Page");
        return "superadmin/playback";
    }

    @GetMapping("/superadmin/replay")
    public String showReplay() {
        logger.info("🔹 Accessed SuperAdmin - Replay Page");
        return "superadmin/replay";
    }

    @GetMapping("/superadmin/map2")
    public String showMap2(Model model) {
        logger.info("🔹 Accessed SuperAdmin - Map2 Report");
        return "superadmin/map2";
    }

    @GetMapping("/superadmin/createdevices")
    public String showCreateDevices(Model model) {
        logger.info("🔹 Accessed SuperAdmin - Create Devices Page");
        return "superadmin/createdevices";
    }

    @GetMapping("/superadmin/event")
    public String showEvent(Model model) {
        logger.info("🔹 Accessed SuperAdmin - Event Report");
        return "superadmin/event";
    }

    @GetMapping("/superadmin/eventreport")
    public String showEventReport(Model model) {
        logger.info("🔹 Accessed SuperAdmin - Event Report Page");
        return "superadmin/eventreport";
    }

    @GetMapping("/superadmin/device-report")
    public String showDeviceReport() {
        logger.info("🔹 Accessed SuperAdmin - Device Report");
        return "superadmin/device_report";
    }
    
    

    @GetMapping("/superadmin/vehicle-violation")
    public String showVehicleViolation() {
        logger.info("🔹 Accessed SuperAdmin - Vehicle Violation Report");
        return "superadmin/vehicle_violation";
    }

    
    
    @GetMapping("/superadmin/driver-violation-report")
    public String showDriverViolationReport() {
        logger.info("🔹 Accessed SuperAdmin - Driver Violation Report");
        return "superadmin/driver_violation_report";
    }

    @GetMapping("/superadmin/renewal")
    public String showRenewal() {
        logger.info("🔹 Accessed SuperAdmin - Renewal Page");
        return "superadmin/renewal";
    }
    
    
    
    
    @GetMapping("/superadmin/OTA")
    public String showOta(Model model) {
        logger.info("🔹 Accessed SuperAdmin - OTA Report");
        return "superadmin/OTA";
    }
    
    

    @GetMapping("/superadmin/dealer_view")
    public String dealerView() {
        return "superadmin/dealer_view"; 
    }

    @GetMapping("/superadmin/admin_view")
    public String adminView() {
        return "superadmin/admin_view"; 
    }

    @GetMapping("/superadmin/client_view")
    public String clientView() {
        return "superadmin/client_view"; 
    }

    @GetMapping("/superadmin/users_view")
    public String usersView() {
        return "superadmin/users_view"; 
    }

    @GetMapping("/superadmin/driver_view")
    public String driverView() {
        return "superadmin/driver_view"; 
    }



    
    @GetMapping("/superadmin/driver-violation")
    public String showDriverViolation() {
        logger.info("🔹 Accessed SuperAdmin - Vehicle Violation Report");
        return "superadmin/driver_violation_report";
    }
    

    @GetMapping("/superadmin/alert")
    public String showAlert() {
        logger.info("🔹 Accessed SuperAdmin - alert   ");
        return "superadmin/alert";
    }
    
    
    
	  @GetMapping("/view_superadmin_complaints")
	    public String   superAdminShowView(Model model) {
	        return "view_superadmin_complaints"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	  
	
	  @GetMapping("/add_rfidto_dealer")
	    public String   rfidtoShowView(Model model) {
	        return "superadmin/add_rfidto_dealer"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	  
	  @GetMapping("/add_rfidto_client")
	    public String   rfidtoShowViewClient(Model model) {
	        return "superadmin/add_rfidto_client"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	  
	  
	  @GetMapping("/serverLogs")
	    public String   ShowVieeServer(Model model) {
	        return "superadmin/serverLogs"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	  
	  

		/*Original Map
		L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
				            attribution: '&copy; OpenStreetMap contributors',
				        }).addTo(this.map);*/
						
						
		/* Map1
		L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
		    attribution: 'Tiles &copy; Esri & contributors',
		    maxZoom: 19
		}).addTo(this.map);*/
		
		/* Map2
		L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
		    attribution: '&copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors & Carto',
		    subdomains: 'abcd',
		    maxZoom: 19
		}).addTo(this.map);*/
}
