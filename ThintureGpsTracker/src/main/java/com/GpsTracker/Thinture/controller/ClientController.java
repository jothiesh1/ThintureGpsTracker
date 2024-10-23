package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.service.AdminService;
import com.GpsTracker.Thinture.service.ClientService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Controller
@RequestMapping("/clients")
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ClientService clientService;
    @GetMapping("/add")
    public String showAddClientForm() {
        logger.info("Displaying the Add Client Form.");
        return "redirect:/add_client.html";  // Redirecting to the static HTML file
    }



    @PostMapping("/add")
    public String addClient(@ModelAttribute Client client, Model model) {
        logger.info("Processing form data for new client: {}", client.getEmail());

        try {
            clientService.saveClient(client);
            model.addAttribute("successMessage", "Client added successfully!");
        } catch (Exception e) {
            logger.error("Error adding client: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error adding client. Please try again.");
        }

        return "add_client"; // Redirect or display success message
    }

    @GetMapping("/all")
    public String getAllClients(Model model) {
        List<Client> clients = clientService.getAllClients();
        model.addAttribute("clients", clients);
        return "client_list"; // Page to list all clients (if needed)
    }
}
