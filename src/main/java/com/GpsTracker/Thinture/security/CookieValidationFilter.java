package com.GpsTracker.Thinture.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CookieValidationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CookieValidationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        logger.info("➡️ Incoming request path: {}", path);

        // ✅ Allow API requests (skip filter if API/static/fetch/etc.)
        if (isApiRequest(path)) {
            logger.info("✅ Skipping filter for API call or static asset: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        if (auth == null) {
            logger.warn("⚠️ No authentication found - skipping filter.");
            filterChain.doFilter(request, response);
            return;
        }

        if (!auth.isAuthenticated()) {
            logger.warn("⚠️ User is not authenticated - skipping filter.");
            filterChain.doFilter(request, response);
            return;
        }

        logger.info("✅ User is authenticated: {}", auth.getName());

        String roleRequired = null;

        // ✅ ONLY block page URLs (example: /dealer/page/**)
        if (path.startsWith("/superadmin/page/")) {
            roleRequired = "SUPERADMIN";
        } else if (path.startsWith("/admin/page/")) {
            roleRequired = "ADMIN";
        } else if (path.startsWith("/dealer/page/")) {
            roleRequired = "DEALER";
        } else if (path.startsWith("/client/page/")) {
            roleRequired = "CLIENT";
        } else if (path.startsWith("/user/page/")) {
            roleRequired = "USER";
        }

        if (roleRequired != null) {
            String cookieUserType = getUserTypeFromCookie(request);
            logger.info("🔎 Path: {}, Required Role: {}, Cookie userType: {}", path, roleRequired, cookieUserType);

            boolean allowed = false;

            if (cookieUserType != null) {
                if (cookieUserType.equalsIgnoreCase(roleRequired)) {
                    allowed = true;
                    logger.info("✅ Access GRANTED: Exact match between cookie and required role.");
                }
            } else {
                logger.warn("⚠️ Cookie 'userType' NOT FOUND.");
            }

            if (!allowed) {
                logger.warn("🚫 Access DENIED: Role mismatch! Redirecting to /access-denied for {}", path);
                response.sendRedirect("/access-denied");
                return;
            }

        } else {
            logger.info("ℹ️ No HTML role check needed for this path: {}", path);
        }

        logger.info("✅ CookieValidationFilter passed for: {}", path);
        filterChain.doFilter(request, response);
    }

    private boolean isApiRequest(String path) {
        // ✅ Skip filter for APIs, static files, and any non-page URLs
        return path.startsWith("/api/")
                || path.endsWith(".js")
                || path.endsWith(".css")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".gif")
                || path.endsWith(".svg")
                || path.endsWith(".ico")
                || path.endsWith(".woff")
                || path.endsWith(".woff2")
                || path.endsWith(".ttf")
                || path.endsWith(".eot")
                // ✅ Skip filter for these API patterns (POSTs etc.)
                || path.contains("/add")
                || path.contains("/save")
                || path.contains("/update")
                || path.contains("/delete")
                || path.contains("/assign")
                || path.contains("/fetch")
                || path.contains("/all");
    }

    private String getUserTypeFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                logger.debug("🍪 Cookie found: {} = {}", cookie.getName(), cookie.getValue());
                if ("userType".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        } else {
            logger.debug("🍪 No cookies found in the request.");
        }
        return null;
    }
}
