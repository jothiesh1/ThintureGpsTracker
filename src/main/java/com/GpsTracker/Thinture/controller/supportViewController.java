package com.GpsTracker.Thinture.controller;





import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;




@Controller
public class supportViewController {


	  @GetMapping("/support")
	    public String supportShowView(Model model) {
	        return "support"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	  

	
	  
	  
	  
	  @GetMapping("/dealer_support")
	    public String   dealerSupportShowView(Model model) {
	        return "dealer_support"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	  
	  
	  
	  @GetMapping("/client_support")
	    public String   clientShowView(Model model) {
	        return "client_support"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	  
	  @GetMapping("/user_support")
	    public String   userShowView(Model model) {
	        return "user_support"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	  
	 
	  @GetMapping("/view_superadmin_complaints")
	    public String   superAdminShowView(Model model) {
	        return "view_superadmin_complaints"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	  


	  @GetMapping("/user_view_client")
	    public String   showClient(Model model) {
	        return "user_view_client"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
	  
	  @GetMapping("/driver_view_client")
	    public String   illShowClient(Model model) {
	        return "driver_view_client"; // Should match the name of the HTML file in the templates folder (map2.html)
	    }
}
