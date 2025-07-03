package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SummaryPageController {

    @GetMapping("/summary")
    public String showSummaryPage() {
        return "summary"; // refers to summary.html in /templates
    }
}
