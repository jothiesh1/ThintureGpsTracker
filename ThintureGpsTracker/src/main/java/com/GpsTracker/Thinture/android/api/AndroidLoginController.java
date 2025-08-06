package com.GpsTracker.Thinture.android.api;

import com.GpsTracker.Thinture.android.dto.AndroidLoginRequest;
import com.GpsTracker.Thinture.android.dto.AndroidLoginResponse;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.service.UserTypeFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/android")
public class AndroidLoginController {

    private static final Logger logger = LoggerFactory.getLogger(AndroidLoginController.class);

    @Autowired
    private UserTypeFilterService userTypeFilterService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public AndroidLoginResponse login(@RequestBody AndroidLoginRequest request) {
        logger.info("📲 Android Login Attempt - Username: {}", request.getUsername());

        var result = userTypeFilterService.findUserAndTypeByEmail(request.getUsername());
        if (result == null) {
            return new AndroidLoginResponse(false, "User not found", null, null);
        }

        String actualUserType = result.actualUserType();
        Object userObj = result.userObject();

        // ✅ Only allow SUPERADMIN
        if (!"SUPERADMIN".equalsIgnoreCase(actualUserType)) {
            logger.warn("🚫 Access denied - Not SUPERADMIN: {}", actualUserType);
            return new AndroidLoginResponse(false, "Only SUPERADMIN is allowed to log in", null, null);
        }

        SuperAdmin superAdmin = (SuperAdmin) userObj;
        if (!superAdmin.isStatus()) {
            return new AndroidLoginResponse(false, "Account is inactive", null, null);
        }

        if (!passwordEncoder.matches(request.getPassword(), superAdmin.getPassword())) {
            return new AndroidLoginResponse(false, "Invalid password", null, null);
        }

        logger.info("✅ Login success for SUPERADMIN: {}", request.getUsername());
        return new AndroidLoginResponse(true, "Login successful", "SUPERADMIN", request.getUsername());
    }
}
