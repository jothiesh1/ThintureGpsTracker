
package com.GpsTracker.Thinture.repository;

import com.GpsTracker.Thinture.model.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
@NoRepositoryBean
public interface BaseRestrictedRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {

    default List<T> findAllRestricted(Long userId, String role) {
        if ("ROLE_SUPERADMIN".equals(role)) {
            return findAll();  // SuperAdmin sees all records
        } else {
            // Apply filtering based on user ID
            return findAll().stream()
                    .filter(entity -> {
                        if ("ROLE_ADMIN".equals(role)) {
                            return userId.equals(entity.getAdmin_id());
                        } else if ("ROLE_DEALER".equals(role)) {
                            return userId.equals(entity.getDealer_id());
                        } else if ("ROLE_USER".equals(role)) {
                            return userId.equals(entity.getUser_id());
                        } else if ("ROLE_CLIENT".equals(role)) {  // ✅ Added client_id filtering
                            return userId.equals(entity.getClient_id());
                        }
                        return false;
                    })
                    .toList();
        }
    }
}
