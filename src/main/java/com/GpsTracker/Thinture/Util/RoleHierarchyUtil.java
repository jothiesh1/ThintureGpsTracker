package com.GpsTracker.Thinture.Util;

import com.GpsTracker.Thinture.dto.RoleHierarchyDTO;
import com.GpsTracker.Thinture.service.UserTypeFilterService.UserTypeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleHierarchyUtil {

    private static final Logger logger = LoggerFactory.getLogger(RoleHierarchyUtil.class);

    public static RoleHierarchyDTO prepareHierarchy(UserTypeResult creator) {
        if (creator == null || creator.getRole() == null) {
            logger.error("❌ Invalid creator: null or role not defined.");
            throw new IllegalArgumentException("Invalid creator details: UserTypeResult or role is null.");
        }

        RoleHierarchyDTO dto = new RoleHierarchyDTO();
        logger.info("🔍 Preparing hierarchy for role: {} (ID: {})", creator.getRole(), creator.getId());

        switch (creator.getRole()) {
            case "SUPERADMIN":
                dto.setSuperadmin_id(creator.getId());
                logger.debug("→ Set superadmin_id: {}", creator.getId());
                break;

            case "ADMIN":
                dto.setAdmin_id(creator.getId());
                dto.setSuperadmin_id(creator.getSuperadminId());
                logger.debug("→ Set admin_id: {}, superadmin_id: {}", creator.getId(), creator.getSuperadminId());
                break;

            case "DEALER":
                dto.setDealer_id(creator.getId());
                dto.setAdmin_id(creator.getAdminId());
                dto.setSuperadmin_id(creator.getSuperadminId());
                logger.debug("→ Set dealer_id: {}, admin_id: {}, superadmin_id: {}", creator.getId(), creator.getAdminId(), creator.getSuperadminId());
                break;

            case "CLIENT":
                dto.setClient_id(creator.getId());
                dto.setDealer_id(creator.getDealerId());
                dto.setAdmin_id(creator.getAdminId());
                dto.setSuperadmin_id(creator.getSuperadminId());
                logger.debug("→ Set client_id: {}, dealer_id: {}, admin_id: {}, superadmin_id: {}",
                        creator.getId(), creator.getDealerId(), creator.getAdminId(), creator.getSuperadminId());
                break;

            case "USER":
                dto.setUser_id(creator.getId());
                dto.setClient_id(creator.getClientId());
                dto.setDealer_id(creator.getDealerId());
                dto.setAdmin_id(creator.getAdminId());
                dto.setSuperadmin_id(creator.getSuperadminId());
                logger.debug("→ Set user_id: {}, client_id: {}, dealer_id: {}, admin_id: {}, superadmin_id: {}",
                        creator.getId(), creator.getClientId(), creator.getDealerId(), creator.getAdminId(), creator.getSuperadminId());
                break;

            default:
                logger.warn("⚠️ Unknown role: {}", creator.getRole());
                throw new IllegalArgumentException("Unknown role: " + creator.getRole());
        }

        return dto;
    }
}
