

package com.GpsTracker.Thinture.controller;




import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class adminPlaybackController {

	
	@GetMapping("/playback_admin")
    public String adminPlayback(Model model) {
        return "playback_admin"; 
    }
}






