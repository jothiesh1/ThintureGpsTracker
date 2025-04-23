package com.GpsTracker.Thinture.aspect;

import com.GpsTracker.Thinture.model.BaseEntity;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import com.GpsTracker.Thinture.security.RoleBasedDataFilterService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import com.GpsTracker.Thinture.model.BaseEntity;
import com.GpsTracker.Thinture.model.SupportTicket;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.repository.VehicleRepository;
import com.GpsTracker.Thinture.security.RoleBasedDataFilterService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Aspect
@Component
public class RoleBasedAccessAspect {

    @Autowired
    private VehicleRepository vehicleRepository;

    private static final Logger logger = LoggerFactory.getLogger(RoleBasedAccessAspect.class);
    private final RoleBasedDataFilterService roleBasedDataFilterService;

    public RoleBasedAccessAspect(RoleBasedDataFilterService roleBasedDataFilterService) {
        this.roleBasedDataFilterService = roleBasedDataFilterService;
    }

    @Around("execution(* com.GpsTracker.Thinture.repository.*.*(..))")
    public Object applyRoleBasedFiltering(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        logger.info("🧩 AOP Detected Method: {}", methodName);

        // ⏩ Skip filtering for login methods
        if (methodName.startsWith("findByEmail")) {
            logger.debug("⏩ Skipping filtering for login method: {}", methodName);
            return joinPoint.proceed();
        }

        // ⏩ Skip autocomplete methods
        if (methodName.equals("findBySerialNoStartingWithIgnoreCase") ||
            methodName.equals("getMatchingSerials") ||
            (methodName.contains("Serial") && methodName.contains("find"))) {
            logger.debug("⏩ Skipping filtering for serial-related method: {}", methodName);
            return joinPoint.proceed();
        }

        // 🚦 Special method for vehicle numbers
        if (methodName.equals("findVehicleNumbersByUserId")) {
            Long userId = (Long) joinPoint.getArgs()[0];
            logger.info("🚦 [Repo] Executing findVehicleNumbersByUserId(userId={})", userId);
            Object result = joinPoint.proceed();
            if (result instanceof List<?> list) {
                if (!list.isEmpty()) {
                    logger.info("✅ [Repo] Vehicle numbers fetched: {}", list);
                    for (Object obj : list) {
                        String vehicleNumber = (String) obj;
                        Optional<Vehicle> optionalVehicle = vehicleRepository.findByVehicleNumber(vehicleNumber);
                        optionalVehicle.ifPresent(vehicle ->
                            logger.info("📡 IMEI for vehicle '{}': {}", vehicle.getVehicleNumber(), vehicle.getImei()));
                    }
                } else {
                    logger.warn("❌ No vehicle numbers found for userId={}", userId);
                }
            }
            return result;
        }

        // 🧠 Fetch user info
        Long userId = roleBasedDataFilterService.getLoggedInUserId();
        String role = roleBasedDataFilterService.getLoggedInUserRole();

        logger.info("🔹 Intercepting method '{}' for role-based filtering. UserID: {}, Role: {}", methodName, userId, role);
        if (userId == null || role == null) {
            logger.warn("⚠️ No authenticated user found. Proceeding without filtering.");
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();

        // ✅ Filter list results
        if (result instanceof List<?> dataList && !dataList.isEmpty()) {
            Object firstItem = dataList.get(0);

            // Case 1: BaseEntity filtering
            if (firstItem instanceof BaseEntity) {
                List<BaseEntity> filtered = dataList.stream()
                        .map(BaseEntity.class::cast)
                        .filter(entity -> roleBasedDataFilterService.isAllowed(entity, userId, role))
                        .collect(Collectors.toList());
                logger.info("✅ Role-based filtering (BaseEntity) applied. Returning {} records.", filtered.size());
                return filtered;
            }

            // Case 2: SupportTicket filtering
            if (firstItem instanceof SupportTicket) {
                List<SupportTicket> filtered = dataList.stream()
                        .map(SupportTicket.class::cast)
                        .filter(ticket -> switch (role) {
                            case "ROLE_ADMIN"      -> userId.equals(ticket.getAdminId());
                            case "ROLE_DEALER"     -> userId.equals(ticket.getDealerId());
                            case "ROLE_CLIENT"     -> userId.equals(ticket.getClientId());
                            case "ROLE_USER"       -> userId.equals(ticket.getUserId());
                            case "ROLE_DRIVER"     -> userId.equals(ticket.getDriverId());
                            case "ROLE_SUPERADMIN" -> userId.equals(ticket.getSuperadminId());
                            default -> false;
                        })
                        .collect(Collectors.toList());
                logger.info("✅ Role-based filtering (SupportTicket) applied. Returning {} records.", filtered.size());
                return filtered;
            }

            // Case 3: String device ID filtering
            if (firstItem instanceof String) {
                List<String> filteredDevices = dataList.stream()
                        .map(String.class::cast)
                        .filter(deviceId -> roleBasedDataFilterService.isDeviceAllowed(deviceId, userId, role))
                        .collect(Collectors.toList());
                logger.info("✅ Role-based device ID filtering applied. Returning {} records.", filteredDevices.size());
                return filteredDevices;
            }
        }

        return result;
    }
}

    
    



/** ░▒▓█████████████████████▓▒░
 * 
 *      Ｊ
 *      Ｏ
 *      Ｔ
 *      Ｈ
 *      Ｉ
 *      Ｅ
 *      Ｓ
 *      Ｈ
 * 
 * Senior Developer, R&D 
 * 
 * ░▒▓█████████████████████▓▒░
 
@Aspect
@Component
public class RoleBasedAccessAspect {

    private static final Logger logger = LoggerFactory.getLogger(RoleBasedAccessAspect.class);
    private final RoleBasedDataFilterService roleBasedDataFilterService;

    public RoleBasedAccessAspect(RoleBasedDataFilterService roleBasedDataFilterService) {
        this.roleBasedDataFilterService = roleBasedDataFilterService;
    }

    @Around("execution(* com.GpsTracker.Thinture.repository.*.*(..))")
    public Object applyRoleBasedFiltering(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        // ⏩ Skip filtering for login-related methods
        if (methodName.startsWith("findByEmail")) {
            logger.debug("⏩ Skipping filtering for login method: {}", methodName);
            return joinPoint.proceed();
        }

        Long userId = roleBasedDataFilterService.getLoggedInUserId();
        String role = roleBasedDataFilterService.getLoggedInUserRole();

        logger.info("🔹 Intercepting method: {} for Role-Based Filtering", methodName);

        if (userId == null || role == null) {
            logger.warn("⚠ No authenticated user found. Skipping filtering.");
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();

        if (result instanceof List<?> dataList && !dataList.isEmpty()) {
            // ✅ Filter BaseEntity-based lists
            if (dataList.get(0) instanceof BaseEntity) {
                List<BaseEntity> filteredData = dataList.stream()
                        .map(e -> (BaseEntity) e)
                        .filter(entity -> roleBasedDataFilterService.isAllowed(entity, userId, role))
                        .collect(Collectors.toList());

                logger.info("✅ Role-based filtering applied. Returning {} records.", filteredData.size());
                return filteredData;
            }

            // ✅ Filter device ID lists (if applicable)
            if (dataList.get(0) instanceof String) {
                List<String> filteredDevices = dataList.stream()
                        .map(String.class::cast)
                        .filter(deviceId -> roleBasedDataFilterService.isDeviceAllowed(deviceId, userId, role))
                        .collect(Collectors.toList());

                logger.info("✅ Device ID filtering applied. Returning {} records.", filteredDevices.size());
                return filteredDevices;
            }
        }

        // ✅ Default fallback if filtering not applied
        return result;
    }
}
*/