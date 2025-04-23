
package com.GpsTracker.Thinture.aspect;

import com.GpsTracker.Thinture.model.BaseEntity;
import com.GpsTracker.Thinture.security.RoleBasedDataFilterService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

        // 🧩 Debug log to show the actual method name
        logger.info("🧩 AOP Detected Method: {}", methodName);

        // ⏩ Skip filtering for login-related methods
        if (methodName.startsWith("findByEmail")) {
            logger.debug("⏩ Skipping filtering for login method: {}", methodName);
            return joinPoint.proceed();
        }

        // ✅ Skip filtering for serial number autocomplete methods
        if (methodName.equals("findBySerialNoStartingWithIgnoreCase") ||
            methodName.equals("getMatchingSerials") ||  // <-- add your actual method name here if different
            methodName.contains("Serial") && methodName.contains("find")) {
            logger.info("⏩ Skipping filtering for serial suggestions: {}", methodName);
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