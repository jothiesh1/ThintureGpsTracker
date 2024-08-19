package com.GpsTracker.Thinture.controller;

//package com.ThintureGpsTracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

  @GetMapping("/map")
  public String showMap() {
      return "map";  // The name of your Thymeleaf template (without the .html extension)
  }
}
//
