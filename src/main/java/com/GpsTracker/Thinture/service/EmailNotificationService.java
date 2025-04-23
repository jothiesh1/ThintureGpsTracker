package com.GpsTracker.Thinture.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);
    private final JavaMailSender mailSender;

    @Value("${app.server.url}")  // Read from application.properties
    private String serverUrl;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = serverUrl + "/reset-password?token=" + token;
        String subject = "Reset Your Password";
        String body = "<h3>Click the link below to reset your password:</h3>"
                    + "<a href='" + resetLink + "'>Reset Password</a>"
                    + "<p>If you did not request this, please ignore this email.</p>";

        logger.info("Sending password reset email to: {}", toEmail);
        sendEmail(toEmail, subject, body);
    }

    public void sendEmail(String to, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // HTML content enabled
            mailSender.send(message);
            logger.info("Email successfully sent to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", to, e);
        }
    }
}


/*
@Service
public class EmailNotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("jothiesh7@gmail.com");  // Sender's email address
        mailSender.send(message);

        System.out.println("Email sent to " + to);
    }
}
*/