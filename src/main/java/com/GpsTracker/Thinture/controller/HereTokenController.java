package com.GpsTracker.Thinture.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GpsTracker.Thinture.service.HereOAuthTokenService;

@RestController
public class HereTokenController {

    private final HereOAuthTokenService tokenService;

    public HereTokenController(HereOAuthTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/here-token")
    public String getToken() {
        return tokenService.fetchAccessToken();
    }
}
