package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DailyChartController {

    @GetMapping("/daily_km_chart")
    public String dailyChartShow(Model model) {
        return "daily_km_chart"; // This maps to src/main/resources/templates/daily_km_chart.html
    }
}
