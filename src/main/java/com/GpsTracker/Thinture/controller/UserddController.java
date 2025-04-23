package com.GpsTracker.Thinture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.model.User;
import com.GpsTracker.Thinture.service.UserService;

import java.util.List;
import java.util.Optional;
//
@Controller
@RequestMapping("/users")
public class UserddController {
	/*

    @Autowired
    private UserService userService;

   
    @GetMapping
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "total_users"; // Ensure this matches the template name
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public String createUser(User user) {
        userService.createUser(user);
        return "redirect:/users";
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> updatedUser = userService.updateUser(id, userDetails);
        return updatedUser.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/add")
    public String showAddUserrForm(Model model) {
        logger.info("Displaying the Add Userr Form");
        model.addAttribute("userr", new User());
        return "add_user"; // Return the HTML form for adding a user
    }

    
  
    
    

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUserr(@PathVariable Long id) {
        logger.info("Received request to delete userr with ID: {}", id);

        try {
            userrService.deleteUserr(id);
            logger.info("Userr deleted successfully!");

            return ResponseEntity.ok("Userr deleted successfully!");
        } catch (Exception e) {
            logger.error("Error occurred while deleting userr", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting userr");
        }
    }
 
    */
}
