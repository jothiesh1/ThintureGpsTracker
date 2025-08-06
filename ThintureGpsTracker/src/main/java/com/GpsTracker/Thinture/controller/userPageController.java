package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class userPageController {

    private static final Logger logger = LoggerFactory.getLogger(userPageController.class);

    @GetMapping("/user/dashboard_user")
    public String showDashboard(Model model) {
        logger.info("🔹 Displaying user/dashboard_user.html");
        return "user/dashboard_user";
    }

    @GetMapping("/user/adddriver_user")
    public String addDriverUser(Model model) {
        logger.info("🔹 Displaying user/adddriver_user.html");
        return "user/adddriver_user";
    }

    @GetMapping("/user/user_map")
    public String userLiveMap(Model model) {
        logger.info("🔹 Displaying user/user_map.html");
        return "user/user_map";
    }

    @GetMapping("/user/user_playback")
    public String userPlayback(Model model) {
        logger.info("🔹 Displaying user/user_playback.html");
        return "user/user_playback";
    }

    @GetMapping("/user/user_event")
    public String userEvent(Model model) {
        logger.info("🔹 Displaying user/user_event.html");
        return "user/user_event";
    }

    @GetMapping("/user/user_event_report")
    public String userEventReport(Model model) {
        logger.info("🔹 Displaying user/user_event_report.html");
        return "user/user_event_report";
    }

    @GetMapping("/user/view_user_complaints")
    public String viewUserComplaints(Model model) {
        logger.info("🔹 Displaying user/view_user_complaints.html");
        return "user/view_user_complaints";
    }

    @GetMapping("/user/user_support")
    public String userSupport(Model model) {
        logger.info("🔹 Displaying user/user_support.html");
        return "user/user_support";
    }

    @GetMapping("/user/user_feedback")
    public String userFeedback(Model model) {
        logger.info("🔹 Displaying user/user_feedback.html");
        return "user/user_feedback";
    }

    @GetMapping("/user/vehicle_violation_user")
    public String vehicleViolationUser(Model model) {
        logger.info("🔹 Displaying user/vehicle_violation_user.html");
        return "user/vehicle_violation_user";
    }
    
    

    @GetMapping("/user/profile_user")
    public String UserProfileView(Model model) {
        logger.info("🔹 Displaying user/vehicle_violation_user.html");
        return "user/profile_user";
    }
    

    @GetMapping("/error")
    public String UserProfilerror (Model model) {
        logger.info("🔹 Displaying user/vehicle_violation_user.html");
        return "error";
    }
    
    /*

    @GetMapping("/user/user_map2")
    public String userLiveMap2(Model model) {
        logger.info("🔹 Displaying user/user_map.html");
        return "user/user_map2";
    }
    
    @GetMapping("/user/user_map3")
    public String userLiveMap3(Model model) {
        logger.info("🔹 Displaying user/user_map.html");
        return "user/user_map3";
    }
    
    */
}

