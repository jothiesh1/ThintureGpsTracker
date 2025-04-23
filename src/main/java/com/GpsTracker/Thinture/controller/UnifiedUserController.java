package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.GpsTracker.Thinture.dto.allUserDTO;
import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.repository.AdminRepository;
import com.GpsTracker.Thinture.repository.ClientRepository;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.DriverRepository;
import com.GpsTracker.Thinture.repository.SuperAdminRepository;
import com.GpsTracker.Thinture.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UnifiedUserController {

    private static final Logger logger = LoggerFactory.getLogger(UnifiedUserController.class);

    @Autowired private SuperAdminRepository superAdminRepo;
    @Autowired private AdminRepository adminRepo;
    @Autowired private DealerRepository dealerRepo;
    @Autowired private ClientRepository clientRepo;
    @Autowired private UserRepository userRepo;
    
    @Autowired private DriverRepository driverRepo;

    @GetMapping("/{role}")
    public ResponseEntity<List<allUserDTO>> getUsersByRole(@PathVariable String role) {
        List<allUserDTO> result;

        switch (role.toLowerCase()) {

            case "admin":
                result = adminRepo.findAll().stream()
                        .map(u -> new allUserDTO(
                            u.getId(),
                            u.getCompanyName(),
                            u.getEmail(),
                            u.getAddress(),
                            u.getPhone(),
                            u.getCountry(),
                            u.isStatus()
                        ))
                        .collect(Collectors.toList());
                break;

            case "dealer":
                result = dealerRepo.findAll().stream()
                        .map(u -> new allUserDTO(
                            u.getId(),
                            u.getCompanyName(),
                            u.getEmail(),
                            u.getAddress(),
                            u.getPhone(),
                            u.getCountry(),
                            u.isStatus()
                        ))
                        .collect(Collectors.toList());
                break;

            case "client":
                result = clientRepo.findAll().stream()
                        .map(u -> new allUserDTO(
                            u.getId(),
                            u.getCompanyName(),
                            u.getEmail(),
                            u.getAddress(),
                            u.getPhone(),
                            u.getCountry(),
                            u.isStatus()
                        ))
                        .collect(Collectors.toList());
                break;

            case "user":
                result = userRepo.findAll().stream()
                        .map(u -> new allUserDTO(
                            u.getId(),
                            u.getCompanyName(),
                            u.getEmail(),
                            u.getAddress(),
                            u.getPhone(),
                            u.getCountry(),
                            u.isStatus()
                        ))
                        .collect(Collectors.toList());
                break;

            case "driver":
                result = driverRepo.findAll().stream()
                        .map(d -> new allUserDTO(
                            d.getId(),
                            d.getFullName(),
                            d.getEmail(),
                            d.getAddress(),
                            d.getContact(),
                            d.getCountry(),
                            d.isStatus(),
                            d.getLicense(),
                            d.getLicenseType(),
                            d.getRfid(),
                            d.getDdp(),
                            d.getDdpExpiry(),
                            d.getVehicle(),
                            d.getDob()
                        ))
                        .collect(Collectors.toList());
                break;


            default:
                return ResponseEntity.badRequest().build();
        }

        logger.info("✅ Fetched {} records for role: {}", result.size(), role);
        return ResponseEntity.ok(result);
    }

}
