package com.projectpulse.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendInvitation(String toEmail, String token, String adminName, String adminEmail, String role, String customMessage) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to Project Pulse - Complete Your Registration");

            String registrationLink = baseUrl + "/register?token=" + token;
            String body = customMessage != null && !customMessage.isBlank()
                    ? customMessage.replace("[Registration link]", registrationLink)
                    : buildDefaultMessage(adminName, adminEmail, registrationLink);

            helper.setText(body, true);
            mailSender.send(message);
            log.info("Invitation sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send invitation to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendTeamAssignmentNotification(String toEmail, String teamName, String studentName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Project Pulse - Team Assignment");
            String body = "<p>Hello " + studentName + ",</p>" +
                    "<p>You have been assigned to team: <strong>" + teamName + "</strong>.</p>" +
                    "<p>Please log in to Project Pulse to get started.</p>";
            helper.setText(body, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send team assignment notification: {}", e.getMessage());
        }
    }

    @Async
    public void sendTeamRemovalNotification(String toEmail, String teamName, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Project Pulse - Team Removal");
            String body = "<p>Hello " + userName + ",</p>" +
                    "<p>You have been removed from team: <strong>" + teamName + "</strong>.</p>";
            helper.setText(body, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send team removal notification: {}", e.getMessage());
        }
    }

    @Async
    public void sendTeamDeletionNotification(String toEmail, String teamName, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Project Pulse - Team Deleted");
            String body = "<p>Hello " + userName + ",</p>" +
                    "<p>Team <strong>" + teamName + "</strong> has been deleted.</p>";
            helper.setText(body, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send team deletion notification: {}", e.getMessage());
        }
    }

    @Async
    public void sendAccountReactivated(String toEmail, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Project Pulse - Account Reactivated");
            String body = "<p>Hello " + name + ",</p><p>Your account has been reactivated. You can now log in.</p>";
            helper.setText(body, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send reactivation email: {}", e.getMessage());
        }
    }

    private String buildDefaultMessage(String adminName, String adminEmail, String registrationLink) {
        return "<p>Hello,</p>" +
                "<p>" + adminName + " has invited you to join <strong>Project Pulse</strong>. " +
                "To complete your registration, please use the link below:</p>" +
                "<p><a href=\"" + registrationLink + "\">" + registrationLink + "</a></p>" +
                "<p>If you have any questions, contact " + adminEmail + ".</p>" +
                "<p>Please note: This email is not monitored.</p>" +
                "<br><p>Best regards,<br>Project Pulse Team</p>";
    }
}
