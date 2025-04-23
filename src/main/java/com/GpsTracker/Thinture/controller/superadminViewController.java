package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class superadminViewController
{
    @GetMapping("/dealer_view")
    public String dealeView() {
        return "dealer_view"; // This refers to admin in the templates directory
    }
    @GetMapping("/admin_view")
    public String adminView() {
        return "admin_view"; // This refers to replay.html in the templates directory
    }
    
    @GetMapping("/client_view")
    public String clientView() {
        return "client_view"; // This refers to replay.html in the templates directory
    }
    
    @GetMapping("/users_view")
    public String usersView() {
        return "users_view"; // This refers to replay.html in the templates directory
    }
    
}