package com.GpsTracker.Thinture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Configuration
public class MailConfig {

    private static final Logger logger = LoggerFactory.getLogger(MailConfig.class);

    @Bean
    public JavaMailSender mailSender() {
        logger.info("🔧 Initializing JavaMailSender...");

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);  // TLS port
        mailSender.setUsername("jothiesh7@gmail.com");  // Your Gmail
        mailSender.setPassword("eqmv bdsx igpl ehjm");  // App-specific password

        logger.debug("📤 Mail Sender configured with:");
        logger.debug("   ➤ Host: {}", mailSender.getHost());
        logger.debug("   ➤ Port: {}", mailSender.getPort());
        logger.debug("   ➤ Username: {}", mailSender.getUsername());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");  // This enables JavaMail debug logs

        logger.info("✅ JavaMailSender successfully configured.");

        return mailSender;
    }
    
    
    /*
     * 
     * 
     * / Server settings
        $mail->Host = 'thin.thinture.interactivedns.com'; // SMTP server
        $mail->Username = 'thinturetest@thin.thinture.interactivedns.com'; // SMTP username
        $mail->Password = 'thinture2024!@'; // SMTP password
        $mail->SMTPSecure = 'tls'; // Encryption type ('tls' or 'ssl')
        $mail->Port = 465; // SMTP port
     */
}
