package com.GpsTracker.Thinture.controller;

import com.GpsTracker.Thinture.model.Userr;
import com.GpsTracker.Thinture.service.UserrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserrController {

    private static final Logger logger = LoggerFactory.getLogger(UserrController.class);

    @Autowired
    private UserrService userrService;

    @GetMapping("/add")
    public String showAddUserrForm(Model model) {
        logger.info("Displaying the Add Userr Form");
        model.addAttribute("userr", new Userr());
        return "add_user"; // Return the HTML form for adding a user
    }

    @PostMapping("/add")
    public ResponseEntity<String> addUserr(@RequestBody Userr userr) {
        logger.info("Received request to add userr: {}", userr);

        try {
            userrService.saveUserr(userr);
            logger.info("Userr added successfully!");

            return ResponseEntity.ok("Userr added successfully!");
        } catch (Exception e) {
            logger.error("Error occurred while adding userr", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding userr");
        }
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
}
