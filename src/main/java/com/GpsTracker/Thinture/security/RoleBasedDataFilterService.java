package com.GpsTracker.Thinture.security;


import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.BaseEntity;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;
import com.GpsTracker.Thinture.repository.VehicleRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;




/*
@Service
public class RoleBasedDataFilterService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public Long getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal() == null || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getId();
        }

        return null;
    }

    public String getLoggedInUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal() == null || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getAuthorities().iterator().next().getAuthority();
        }

        return null;
    }

    public boolean isAllowed(BaseEntity entity, Long userId, String role) {
        if (entity == null || userId == null || role == null) {
            return false;
        }

        boolean allowed;
        switch (role) {
            case "ROLE_SUPERADMIN":
                allowed = true;
                break;
            case "ROLE_ADMIN":
                allowed = userId.equals(entity.getAdmin_id());
                break;
            case "ROLE_DEALER":
                allowed = userId.equals(entity.getDealer_id());
                break;
            case "ROLE_USER":
                allowed = userId.equals(entity.getUser_id());
                break;
            case "ROLE_CLIENT":
                allowed = userId.equals(entity.getClient_id());
                break;
            case "ROLE_DRIVER":
                allowed = userId.equals(entity.getDriver_id());
                break;
            default:
                allowed = false;
                break;
        }

        return allowed;
    }

    public boolean isDeviceAllowed(String deviceId, Long userId, String role) {
        if ("ROLE_SUPERADMIN".equals(role)) {
            return true;
        }

        Optional<Vehicle> vehicle = vehicleRepository.findByDeviceID(deviceId);

        if (vehicle.isPresent()) {
            Vehicle v = vehicle.get();

            Long adminId = (v.getAdmin() != null) ? v.getAdmin().getId() : null;
            Long dealerId = (v.getDealer() != null) ? v.getDealer().getId() : null;
            Long clientId = (v.getClient() != null) ? v.getClient().getId() : null;

            switch (role) {
                case "ROLE_ADMIN":
                    return adminId != null && adminId.equals(userId);
                case "ROLE_DEALER":
                    return dealerId != null && dealerId.equals(userId);
                case "ROLE_CLIENT":
                    return clientId != null && clientId.equals(userId);
                default:
                    return false;
            }
        }
        return false;
    }
}


*/
/*
 * 
 * loggers disable code
 * 
 */
@Service
public class RoleBasedDataFilterService {

    private static final Logger logger = LoggerFactory.getLogger(RoleBasedDataFilterService.class);

    @Autowired
    private VehicleRepository vehicleRepository;

    public Long getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.warn("⚠ No authenticated user found in SecurityContext.");
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            Long userId = ((CustomUserDetails) principal).getId();
            logger.info("🔹 Retrieved Logged-in User ID: {}", userId);
            return userId;
        }

        logger.warn("⚠ User authentication principal is invalid. Principal Type: {}", principal.getClass().getName());
        return null;
    }

    public String getLoggedInUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.warn("⚠ No authenticated user role found in SecurityContext.");
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            String role = ((CustomUserDetails) principal).getAuthorities().iterator().next().getAuthority();
            logger.info("🔹 Retrieved Logged-in User Role: {}", role);
            return role;
        }

        logger.warn("⚠ User authentication principal role is invalid. Principal Type: {}", principal.getClass().getName());
        return null;
    }

    // ✅ **Check if user is allowed to access the entity**
 // ✅ **Check if user is allowed to access the entity**
    public boolean isAllowed(BaseEntity entity, Long userId, String role) {
        if (entity == null || userId == null || role == null) {
            logger.warn("⚠ Invalid parameters for role-based access check.");
            return false;
        }

        boolean allowed;
        switch (role) {
            case "ROLE_SUPERADMIN":
                allowed = true;
                break;
            case "ROLE_ADMIN":
                allowed = userId.equals(entity.getAdmin_id());
                break;
            case "ROLE_DEALER":
                allowed = userId.equals(entity.getDealer_id());
                break;
            case "ROLE_USER":
                allowed = userId.equals(entity.getUser_id()); // ✅ Updated for User
                break;
            case "ROLE_CLIENT":  // ✅ Added client_id filtering
                allowed = userId.equals(entity.getClient_id());
                break;
            case "ROLE_DRIVER":
                allowed = userId.equals(entity.getDriver_id());
                break;
            default:
                allowed = false;
                break;
        }

        if (!allowed) {
            logger.warn("🚫 Unauthorized access attempt. Role: {} | User ID: {} | Entity ID: {}", role, userId, entity.getId());
        } else {
            logger.info("✅ Access granted to Role: {} | User ID: {} | Entity ID: {}", role, userId, entity.getId());
        }

        return allowed;
    }

    public boolean isDeviceAllowed(String deviceId, Long userId, String role) {
        // SuperAdmin has full access to all devices
        if ("ROLE_SUPERADMIN".equals(role)) {
            return true;
        }

        Optional<Vehicle> vehicle = vehicleRepository.findByDeviceID(deviceId);

        if (vehicle.isPresent()) {
            Vehicle v = vehicle.get();

            // Extract related entity IDs
            Long adminId = (v.getAdmin() != null) ? v.getAdmin().getId() : null;
            Long dealerId = (v.getDealer() != null) ? v.getDealer().getId() : null;
            Long clientId = (v.getClient() != null) ? v.getClient().getId() : null;
      //      Long driverId = (v.getDriver() != null) ? v.getDriver().getId() : null;

            // Role-based access check
            switch (role) {
                case "ROLE_ADMIN":
                    return adminId != null && adminId.equals(userId);
                case "ROLE_DEALER":
                    return dealerId != null && dealerId.equals(userId);
                case "ROLE_CLIENT":
                    return clientId != null && clientId.equals(userId);
               // case "ROLE_DRIVER":
                 //   return driverId != null && driverId.equals(userId);
                default:
                    return false;
            }
        }
        return false;
    }


}


