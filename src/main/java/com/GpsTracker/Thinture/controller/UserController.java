package com.GpsTracker.Thinture.controller;


import com.GpsTracker.Thinture.model.User;
import com.GpsTracker.Thinture.security.CustomUserDetails;
import com.GpsTracker.Thinture.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

  

    /**
     * ✅ Adds a new user and assigns the currently logged-in user's ID (Admin, Dealer, SuperAdmin, Client).
     */
    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            logger.info("📥 Add User Request by: {} (ID: {}, Roles: {})", 
                        userDetails.getUsername(), userDetails.getId(), userDetails.getAuthorities());

            User savedUser = userService.addUser(user);
            logger.info("✅ User added successfully with ID: {}", savedUser.getId());
            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            logger.error("❌ Error adding user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding user");
        }
    }

  
    
    

   
    
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        System.out.println("🔥 /users/api/all endpoint hit"); // <-- TEMP log
        logger.info("🔥 /users/api/all endpoint hit"); // <-- LOGGER log

        try {
            List<User> users = userService.getAllUsers();

            List<Map<String, Object>> response = users.stream().map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("name", user.getCompanyName()); // Make sure this is not null!
                return userMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ Error fetching users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

 // 📝 Edit user
    @PutMapping("/edit/{id}")
    public ResponseEntity<String> editUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            userService.updateUser(id, updatedUser);
            logger.info("✅ User updated: ID {}", id);
            return ResponseEntity.ok("User updated successfully!");
        } catch (Exception e) {
            logger.error("❌ Error updating user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user.");
        }
    }

    // 🔁 Toggle status
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<String> toggleUserStatus(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userService.toggleStatus(id);
            if (userOpt.isPresent()) {
                boolean status = userOpt.get().isStatus();
                return ResponseEntity.ok(status ? "Unlocked (Active)" : "Locked (Inactive)");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            logger.error("❌ Error toggling user status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error toggling status");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUserr(@PathVariable Long id) {
        logger.info("Received request to delete userr with ID: {}", id);

        try {
            userService.deleteUserr(id);
            logger.info("Userr deleted successfully!");

            return ResponseEntity.ok("Userr deleted successfully!");
        } catch (Exception e) {
            logger.error("Error occurred while deleting userr", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting userr");
        }
    }
}
