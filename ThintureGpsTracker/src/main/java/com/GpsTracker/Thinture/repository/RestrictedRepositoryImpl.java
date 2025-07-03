
package com.GpsTracker.Thinture.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.GpsTracker.Thinture.model.BaseEntity;
import com.GpsTracker.Thinture.security.RoleBasedDataFilterService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

/** ❖❖❖❖❖❖❖❖❖❖❖❖❖❖❖❖❖❖❖❖❖❖❖❖❖
 * Author: 𝒥𝑜𝓉𝒽𝒾𝑒𝓈𝒽 ✵✵✵✵✵✵✵
 * Senior Developer, R&D 
 *
 * ✺✺✺✺✺✺✺✺✺✺✺✺✺✺✺✺✺✺✺✺✺✺✺✺✺
 */
@Repository
@Transactional
public class RestrictedRepositoryImpl<T extends BaseEntity> {

    private static final Logger logger = LoggerFactory.getLogger(RestrictedRepositoryImpl.class);
    private final EntityManager entityManager;
    private final RoleBasedDataFilterService roleBasedDataFilterService;

    public RestrictedRepositoryImpl(EntityManager entityManager, RoleBasedDataFilterService roleBasedDataFilterService) {
        this.entityManager = entityManager;
        this.roleBasedDataFilterService = roleBasedDataFilterService;
    }

    public List<T> findAllRestricted(Class<T> entityType) {
        Long userId = roleBasedDataFilterService.getLoggedInUserId();
        String role = roleBasedDataFilterService.getLoggedInUserRole();

        logger.debug("🔍 User ID from context: {}", userId);
        logger.debug("🔍 Role from context: {}", role);
        logger.debug("🔍 Entity Type: {}", entityType.getSimpleName());

        if (userId == null || role == null) {
            logger.warn("🚫 Access Denied: No authenticated user.");
            throw new SecurityException("Access denied: No authenticated user.");
        }

        String queryString = "SELECT e FROM " + entityType.getSimpleName() + " e WHERE ";

        switch (role) {
            case "ROLE_ADMIN":
                queryString += "e.admin_id = :userId";
                break;
            case "ROLE_DEALER":
                queryString += "e.dealer_id = :userId";
                break;
            case "ROLE_USER":
                queryString += "e.user_id = :userId";
                break;
            case "ROLE_CLIENT":
                queryString += "e.client_id = :userId";
                break;
            case "ROLE_DRIVER":
                queryString += "e.driver_id = :userId";
                break;
            default:
                logger.info("🟢 SuperAdmin or unrestricted role detected: {}", role);
                return entityManager.createQuery("SELECT e FROM " + entityType.getSimpleName() + " e", entityType)
                        .getResultList();
        }

        logger.debug("🛠 Final Query: {}", queryString);

        TypedQuery<T> query = entityManager.createQuery(queryString, entityType);
        query.setParameter("userId", userId);

        List<T> results = query.getResultList();
        logger.info("✅ Restricted result count for {}: {}", entityType.getSimpleName(), results.size());

        return results;
    }

}