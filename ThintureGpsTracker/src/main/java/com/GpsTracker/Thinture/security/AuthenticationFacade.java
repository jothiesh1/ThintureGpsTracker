package com.GpsTracker.Thinture.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFacade.class);

    // ✅ Get currently logged-in user ID (Admin, Dealer, Client, etc.)
    public Long getAuthenticatedUserId() {
        CustomUserDetails userDetails = getAuthenticatedUserDetails();
        if (userDetails == null) return null;

        Long userId = userDetails.getId();
        logger.info("🔐 [AUTH SUCCESS] Retrieved User ID: {}", userId);
        return userId;
    }

    // ✅ Get currently logged-in user role
    public String getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getAuthorities() == null) {
            logger.warn("⚠️ [AUTH] No user role found in SecurityContext.");
            return null;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
    }

    // ✅ Utility: Get the full CustomUserDetails object (optional use)
    public CustomUserDetails getAuthenticatedUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            logger.warn("⚠️ [AUTH] No authentication object found in SecurityContext.");
            return null;
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            logger.warn("⚠️ [AUTH] Invalid user principal: {}", authentication.getPrincipal());
            return null;
        }

        return userDetails;
    }
    
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    
 // ✅ Add this to get the logged-in user's email (username)
    public String getAuthenticatedEmail() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            logger.warn("⚠️ [AUTH] No authentication object found in SecurityContext.");
            return null;
        }
        return authentication.getName(); // Returns the email or username
    }

}
