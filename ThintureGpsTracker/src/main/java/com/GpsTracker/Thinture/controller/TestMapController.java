package com.GpsTracker.Thinture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestMapController {

    @GetMapping("/testmap")
    public String testShow(Model model) {
        return "testmap";  // This maps to testmap.html inside templates folder
    }
}
