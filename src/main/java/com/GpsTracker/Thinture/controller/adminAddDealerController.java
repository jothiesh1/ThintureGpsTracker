package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class adminAddDealerController {

    private static final Logger logger = LoggerFactory.getLogger(adminAddDealerController.class);

    @GetMapping("/addclient_dealer")
    public String addclientdealerShow(Model model) {
        logger.info("Accessed /addclient_dealer page");
        return "/addclient_dealer"; 
    } 

    @GetMapping("/adddealer_admin")
    public String dealerAdmin(Model model) {
        logger.info("Accessed /adddealer_admin page");
        return "adddealer_admin"; 
    }

    @GetMapping("/vehicle_violation_admin")
    public String vehicle_violation_admin(Model model) {
        logger.info("Accessed /vehicle_violation_admin page");
        return "vehicle_violation_admin"; 
    }

    @GetMapping("/vehicle_violation_user")
    public String vehicle_violation_user(Model model) {
        logger.info("Accessed /vehicle_violation_user page");
        return "/vehicle_violation_user"; 
    }
}
