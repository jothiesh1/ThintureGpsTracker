package com.GpsTracker.Thinture.service;
import java.io.IOException;
import java.util.Base64;

import javax.activation.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;


@Service
public class MailService {

    private final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject, String body, String base64Image) {
        logger.info("Preparing to send email to {}", to);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // 'true' for multipart message
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // True indicates HTML content

            // If there is a screenshot (in base64), decode it and attach
            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] imageBytes = decodeBase64Image(base64Image);
                if (imageBytes != null) {
                    ByteArrayDataSource dataSource = new ByteArrayDataSource(imageBytes, "image/png");
                    helper.addAttachment("map-screenshot.png", dataSource);  // Attach screenshot
                    logger.info("Screenshot attached to the email.");
                } else {
                    logger.error("Failed to decode base64 screenshot.");
                }
            }

            mailSender.send(message);
            logger.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to {}", to, e);
        }
    }

    private byte[] decodeBase64Image(String base64Image) {
        try {
            String base64String = base64Image.split(",")[1];  // Remove the data URL header if present
            return Base64.getDecoder().decode(base64String);
        } catch (Exception e) {
            logger.error("Error decoding base64 image", e);
            return null;
        }
    }
}
