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
import java.util.Objects;
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


/*
@Aspect
@Component
public class RoleBasedAccessAspect {

    @Autowired
    private VehicleRepository vehicleRepository;

    private final RoleBasedDataFilterService roleBasedDataFilterService;

    public RoleBasedAccessAspect(RoleBasedDataFilterService roleBasedDataFilterService) {
        this.roleBasedDataFilterService = roleBasedDataFilterService;
    }

    @Around("execution(* com.GpsTracker.Thinture.repository.*.*(..))")
    public Object applyRoleBasedFiltering(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        if (methodName.startsWith("findByEmail")) {
            return joinPoint.proceed();
        }

        Long userId = roleBasedDataFilterService.getLoggedInUserId();
        String role = roleBasedDataFilterService.getLoggedInUserRole();

        if (userId == null || role == null) {
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();

        if (result instanceof List<?> dataList) {
            if (dataList.isEmpty()) {
                return dataList;
            }

            dataList = dataList.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (dataList.isEmpty()) {
                return dataList;
            }

            Object firstItem = dataList.get(0);

            // Case 1: BaseEntity filtering
            if (firstItem instanceof BaseEntity) {
                List<BaseEntity> filtered = dataList.stream()
                        .map(BaseEntity.class::cast)
                        .filter(e -> roleBasedDataFilterService.isAllowed(e, userId, role))
                        .collect(Collectors.toList());
                return filtered;
            }

            // Case 2: SupportTicket filtering
            if (firstItem instanceof com.GpsTracker.Thinture.model.SupportTicket) {
                List<com.GpsTracker.Thinture.model.SupportTicket> filtered = dataList.stream()
                        .map(com.GpsTracker.Thinture.model.SupportTicket.class::cast)
                        .filter(t -> switch (role) {
                            case "ROLE_ADMIN" -> userId.equals(t.getAdminId());
                            case "ROLE_DEALER" -> userId.equals(t.getDealerId());
                            case "ROLE_CLIENT" -> userId.equals(t.getClientId());
                            case "ROLE_USER" -> userId.equals(t.getUserId());
                            case "ROLE_DRIVER" -> userId.equals(t.getDriverId());
                            case "ROLE_SUPERADMIN" -> userId.equals(t.getSuperadminId());
                            default -> false;
                        })
                        .collect(Collectors.toList());
                return filtered;
            }

            // Case 3: String filtering (DeviceID or SerialNo)
            if (firstItem instanceof String) {
                List<String> filteredStrings = dataList.stream()
                        .map(String.class::cast)
                        .filter(value -> {
                            if (value == null || value.trim().isEmpty()) {
                                return false;
                            }

                            String trimmed = value.trim();

                            List<Vehicle> serialMatches = vehicleRepository.findBySerialNo(trimmed);
                            if (!serialMatches.isEmpty()) {
                                boolean allowed = serialMatches.stream()
                                        .anyMatch(v -> roleBasedDataFilterService.isAllowed(v, userId, role));
                                if (allowed) return true;
                            }

                            Optional<Vehicle> deviceMatch = vehicleRepository.findByDeviceID(trimmed);
                            if (deviceMatch.isPresent()) {
                                Vehicle v = deviceMatch.get();
                                return roleBasedDataFilterService.isAllowed(v, userId, role);
                            }

                            return false;
                        })
                        .collect(Collectors.toList());

                return filteredStrings;
            }
        }

        return result;
    }
}

*/

/*
 * 
 * 
 * loggers disbale code
 * 
 */
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
        logger.info("🧩 [AOP] Detected Method Call: {}", methodName);

        // Skip login methods
        if (methodName.startsWith("findByEmail")) {
            logger.debug("⏩ Skipping filtering for login method: {}", methodName);
            return joinPoint.proceed();
        }

        // Fetch logged-in user
        Long userId = roleBasedDataFilterService.getLoggedInUserId();
        String role = roleBasedDataFilterService.getLoggedInUserRole();

        logger.info("🔐 Logged-in User => ID: {}, Role: {}", userId, role);

        if (userId == null || role == null) {
            logger.warn("⚠️ User not authenticated, skipping filtering.");
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();

        if (result instanceof List<?> dataList) {

            if (dataList.isEmpty()) {
                logger.warn("⚠️ No records found (empty list) for method '{}'. Returning empty list.", methodName);
                return dataList; // Return safely if list is empty
            }

            // ✅ Fix: filter out nulls BEFORE accessing firstItem
            dataList = dataList.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (dataList.isEmpty()) {
                logger.error("❌ All items were NULL after filtering for method '{}'. Returning empty list.", methodName);
                return dataList;
            }

            Object firstItem = dataList.get(0);
            logger.info("📦 Received {} records. First record type: {}", dataList.size(), firstItem.getClass().getSimpleName());

            // Case 1: BaseEntity filtering
            if (firstItem instanceof BaseEntity) {
                logger.info("📋 Listing all BaseEntity records BEFORE filtering:");
                dataList.stream()
                        .map(BaseEntity.class::cast)
                        .forEach(e -> logger.info("📄 Entity ID={} | AdminID={}, DealerID={}, ClientID={}, UserID={}, SuperAdminID={}",
                                e.getId(), e.getAdmin_id(), e.getDealer_id(), e.getClient_id(), e.getUser_id(), e.getSuperadmin_id()));

                List<BaseEntity> filtered = dataList.stream()
                        .map(BaseEntity.class::cast)
                        .filter(e -> {
                            boolean allowed = roleBasedDataFilterService.isAllowed(e, userId, role);
                            logger.info("🔎 Checking EntityID={} => Allowed: {}", e.getId(), allowed);
                            return allowed;
                        })
                        .collect(Collectors.toList());

                logger.info("✅ BaseEntity filtering complete. Records allowed: {}", filtered.size());
                return filtered;
            }

            // Case 2: SupportTicket filtering
            if (firstItem instanceof com.GpsTracker.Thinture.model.SupportTicket) {
                List<com.GpsTracker.Thinture.model.SupportTicket> filtered = dataList.stream()
                        .map(com.GpsTracker.Thinture.model.SupportTicket.class::cast)
                        .filter(t -> switch (role) {
                            case "ROLE_ADMIN" -> userId.equals(t.getAdminId());
                            case "ROLE_DEALER" -> userId.equals(t.getDealerId());
                            case "ROLE_CLIENT" -> userId.equals(t.getClientId());
                            case "ROLE_USER" -> userId.equals(t.getUserId());
                            case "ROLE_DRIVER" -> userId.equals(t.getDriverId());
                            case "ROLE_SUPERADMIN" -> userId.equals(t.getSuperadminId());
                            default -> false;
                        })
                        .collect(Collectors.toList());
                logger.info("✅ SupportTicket filtering complete. Records allowed: {}", filtered.size());
                return filtered;
            }

            // Case 3: String filtering (DeviceID or SerialNo)
            if (firstItem instanceof String) {
                logger.info("🛰️ Starting string filtering (SerialNo + DeviceID check)...");

                List<String> filteredStrings = dataList.stream()
                        .map(String.class::cast)
                        .filter(value -> {
                            if (value == null || value.trim().isEmpty()) {
                                logger.warn("⚠️ Empty string encountered, skipping.");
                                return false;
                            }

                            String trimmed = value.trim();
                            logger.info("🔎 Checking String: '{}'", trimmed);

                            // Check Serial No
                            List<Vehicle> serialMatches = vehicleRepository.findBySerialNo(trimmed);
                            if (!serialMatches.isEmpty()) {
                                for (Vehicle v : serialMatches) {
                                    logger.info("🛠️ Serial Match - Vehicle ID: {} | SerialNo: {} | AdminID: {}, DealerID: {}, ClientID: {}, UserID: {}, SuperAdminID={}",
                                            v.getId(), trimmed, v.getAdmin_id(), v.getDealer_id(), v.getClient_id(), v.getUser_id(), v.getSuperadmin_id());
                                }
                                boolean allowed = serialMatches.stream()
                                        .anyMatch(v -> roleBasedDataFilterService.isAllowed(v, userId, role));
                                logger.info("🎯 SerialNo '{}' access => {}", trimmed, allowed);
                                if (allowed) return true;
                            } else {
                                logger.warn("📭 No vehicle found with SerialNo '{}'", trimmed);
                            }

                            // Check Device ID
                            Optional<Vehicle> deviceMatch = vehicleRepository.findByDeviceID(trimmed);
                            if (deviceMatch.isPresent()) {
                                Vehicle v = deviceMatch.get();
                                logger.info("📦 DeviceID Match - Vehicle ID: {} | DeviceID: {} | AdminID: {}, DealerID: {}, ClientID: {}, UserID: {}, SuperAdminID={}",
                                        v.getId(), trimmed, v.getAdmin_id(), v.getDealer_id(), v.getClient_id(), v.getUser_id(), v.getSuperadmin_id());

                                boolean allowed = roleBasedDataFilterService.isAllowed(v, userId, role);
                                logger.info("🎯 DeviceID '{}' access => {}", trimmed, allowed);
                                return allowed;
                            } else {
                                logger.warn("❌ No vehicle found with DeviceID '{}'", trimmed);
                            }

                            return false;
                        })
                        .collect(Collectors.toList());

                logger.info("✅ String-based filtering complete. Total allowed strings: {}", filteredStrings.size());
                return filteredStrings;
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