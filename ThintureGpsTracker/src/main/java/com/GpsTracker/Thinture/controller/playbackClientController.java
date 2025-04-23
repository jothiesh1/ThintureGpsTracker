package com.GpsTracker.Thinture.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ch.qos.logback.core.model.Model;

@Controller
public class playbackClientController {

	
	@GetMapping("/playback_client")
    public String dealerPlayBack(Model model) {
        return "playback_client"; // Should match the name of the HTML file in the templates folder (map2.html)
    }
}
