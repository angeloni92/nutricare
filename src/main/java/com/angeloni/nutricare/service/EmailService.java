package com.angeloni.nutricare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
	private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email with the specified recipient, subject, and body content.
     * <p>
     * This method creates a MIME email message, sets the provided recipient, subject, and body, 
     * and sends the email using the configured {@link JavaMailSender}.
     * 
     * @param to      the recipient's email address. Must not be {@code null} or empty.
     * @param subject the subject of the email. Must not be {@code null} or empty.
     * @param body    the HTML content of the email body. Must not be {@code null}.
     * 
     * @throws MessagingException if there is an issue while constructing or sending the email.
     * @throws IllegalArgumentException if any of the parameters are {@code null} or empty.
     * 
     * @see MimeMessage
     * @see MimeMessageHelper
     * @see JavaMailSender
     */
    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        helper.setText(body, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom(from);

        mailSender.send(message);
    }
}

