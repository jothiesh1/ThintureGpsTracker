package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.config.TwilioConfig;

//package com.thinture.gpstracker.service;

//import com.thinture.gpstracker.config.TwilioConfig;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/** ───────────✦✧✦───────────
 * Author: 𝙅𝙤𝙩𝙝𝙞𝙚𝙨𝙝 ❖❖❖❖❖❖
 * Senior Developer, R&D 
 *
 * ───────────✦✧✦───────────
 */
@Service
public class SmsNotificationService {

    @Autowired
    private TwilioConfig twilioConfig;

    public void sendSms(String toPhone, String messageBody) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(toPhone),
                    new PhoneNumber(twilioConfig.getTwilioPhoneNumber()),
                    messageBody
            ).create();

            System.out.println("SMS sent successfully to " + toPhone);
        } catch (Exception e) {
            System.err.println("Error sending SMS: " + e.getMessage());
        }
    }
}
