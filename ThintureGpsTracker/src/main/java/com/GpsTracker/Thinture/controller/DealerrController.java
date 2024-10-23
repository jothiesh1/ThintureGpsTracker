package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class DealerrController {

    @GetMapping("/dashboard_dealer")
    public String dealerDashboard() {
        return "dashboard_dealer";  // This should match the name of the Thymeleaf or JSP view file
    }
}

