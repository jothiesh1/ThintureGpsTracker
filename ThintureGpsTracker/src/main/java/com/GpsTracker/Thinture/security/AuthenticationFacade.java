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

    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            logger.warn("⚠️ [AUTH] No authentication object found in SecurityContext.");
            return null;
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            logger.warn("⚠️ [AUTH] Invalid user principal detected: {}", authentication.getPrincipal());
            return null;
        }

        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        logger.info("🔐 [AUTH SUCCESS] Retrieved User ID: {}", userId);
        return userId;
    }

    public String getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || authentication.getAuthorities() == null) {
            logger.warn("⚠️ [AUTH] No user role found in SecurityContext.");
            return null;
        }

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        logger.info("🔑 [ROLE SUCCESS] Retrieved User Role: {}", role);
        return role;
    }
}

