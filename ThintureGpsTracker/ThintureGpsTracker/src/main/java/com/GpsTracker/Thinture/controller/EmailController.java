package com.GpsTracker.Thinture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;

import com.GpsTracker.Thinture.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
@RestController
@RequestMapping("/api")
public class EmailController {

    private final Logger logger = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private MailService mailService;

    @PostMapping("/send-mail")
    public String sendMail(@RequestBody Map<String, String> mailRequest) {
        logger.info("Received email request");

        try {
            String subject = mailRequest.get("subject");
            String body = mailRequest.get("body");
            String screenshot = mailRequest.get("screenshot"); // Screenshot data

            // Log the email subject and body for reference
            logger.info("Email subject: {}", subject);
            logger.info("Email body: {}", body);

            // Set your default email address here (or multiple emails if needed)
            String toEmail1 = "jothiesh7@gmail.com";
            // Send email with screenshot attached
            mailService.sendMail(toEmail1, subject, body, screenshot);
      
            logger.info("Email sent successfully to both emails.");
            return "Email sent successfully";
        } catch (Exception e) {
            logger.error("Failed to send email", e);
            return "Failed to send email";
        }
    }
}
