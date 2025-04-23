package com.GpsTracker.Thinture.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class DefaultController {
//    @GetMapping("/default")
//    public String defaultAfterLogin(HttpServletRequest request) {
//        // Redirect users based on their roles
//        if (request.isUserInRole("SUPERADMIN") || request.isUserInRole("ADMIN")) {
//            return "redirect:/superadmin/dashboard"; // Adjust as per your route
//        } else if (request.isUserInRole("DEALER")) {
//            return "redirect:/total_users.html";
//        } else if (request.isUserInRole("CLIENT")) {
//            return "redirect:/dashboard";
//        } else if (request.isUserInRole("USER")) {
//            return "redirect:/map.html";
//        }
//        return "redirect:/"; // Fallback route
//    }
}
