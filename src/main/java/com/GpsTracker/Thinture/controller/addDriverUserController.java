package com.GpsTracker.Thinture.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.GpsTracker.Thinture.controller.SuperAdminPageController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Controller
public class addDriverUserController {

	private static final Logger logger = LoggerFactory.getLogger(addDriverUserController.class);
	
	@GetMapping("/adddriver_user")
    public String addDriveUser(Model model) {
		   logger.info("Displaying the adddriver_user Form");
        return "/adddriver_user"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
}
