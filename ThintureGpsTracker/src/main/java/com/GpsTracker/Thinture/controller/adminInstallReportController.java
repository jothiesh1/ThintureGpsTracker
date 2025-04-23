package com.GpsTracker.Thinture.controller;




import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class adminInstallReportController {

	
	@GetMapping("/install_report_admin")
    public String adminInstallReport(Model model) {
        return "install_report_admin"; 
    }
}







