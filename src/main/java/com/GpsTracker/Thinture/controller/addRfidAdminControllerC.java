package com.GpsTracker.Thinture.controller;

import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class addRfidAdminControllerC {


	private static final Logger logger = LoggerFactory.getLogger(addRfidAdminControllerC.class);



@GetMapping("/addrfid_admin_dealer")
public String createdeviff(Model model) {
    return "addrfid_admin_dealer"; // Should match the name of the HTML file in the templates folder (map2.html)
}

@GetMapping("/addrfid_admin_client")
public String created(Model model) {
    return "addrfid_admin_client"; // Should match the name of the HTML file in the templates folder (map2.html)
}

@GetMapping("/addrfid_dealer_client")
public String addrfid_dealer_client(Model model) {
    return "addrfid_dealer_client"; // Should match the name of the HTML file in the templates folder (map2.html)
}



}
