package com.GpsTracker.Thinture.alert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.GpsTracker.Thinture.service.EmailNotificationService;
import com.GpsTracker.Thinture.service.SmsNotificationService;

//package com.thinture.gpstracker.alert;

//import com.thinture.gpstracker.service.EmailNotificationService;
//import com.thinture.gpstracker.service.SmsNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlertManager {

    @Autowired
    private EmailNotificationService emailNotificationService;

    @Autowired
    private SmsNotificationService smsNotificationService;

    public void notifyServerDown(String errorDetails) {
        String subject = "Alert: Server Down!";
        String body = "The server encountered an issue:\n\n" + errorDetails;

        // Send Email
        emailNotificationService.sendEmail("jothiesh7@gmail.com", subject, body);

        // Send SMS
        smsNotificationService.sendSms("+91 6380900348", body); // Replace with the recipient's phone number
    }
}
