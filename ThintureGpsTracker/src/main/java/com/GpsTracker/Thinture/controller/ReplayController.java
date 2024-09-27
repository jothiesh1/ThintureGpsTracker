package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReplayController {

    @GetMapping("/replay")
    public String replayPage() {
        return "replay"; // This refers to replay.html in the templates directory
    }
}