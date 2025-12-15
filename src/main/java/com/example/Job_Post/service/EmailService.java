package com.example.Job_Post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    // private static final String FRONTEND_URL = "http://localhost:5173";
    private static final String FRONTEND_URL = "https://kontral.onrender.com";
    private static final String BACKEND_URL = "https://kontral-backend.onrender.com";

    public void sendVerificationEmail(String email, String verificationToken) {
        String subject = "Email Verification";
        String path = "/api/v1/user/register/verify";
        String message = "Click the button below to verify your email address:";

        System.out.println("\nHere\n");
        sendEmail(email, verificationToken, subject, path, message, BACKEND_URL);
    }


    public void sendForgotPasswordEmail(String email, String resetToken) {
        String subject = "Password Reset Request";
        String path = "/reset-password";
        String message = "Click the button below to reset your password:";
        sendEmail(email, resetToken, subject, path, message, FRONTEND_URL);
    }


    private void sendEmail(String email, String token, String subject, String path, String message, String urlString) {
        try {
            String actionUrl = ServletUriComponentsBuilder.fromUriString(urlString)
                    .path(path)
                    .queryParam("token", token)
                    .queryParam("email", email)
                    .toUriString();

            String content = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border-radius: 8px; background-color: #f9f9f9; text-align: center;">
                        <h2 style="color: #333;">%s</h2>
                        <p style="font-size: 16px; color: #555;">%s</p>
                        <a href="%s" style="display: inline-block; margin: 20px 0; padding: 10px 20px; font-size: 16px; color: #fff; background-color: #007bff; text-decoration: none; border-radius: 5px;">Proceed</a>
                        <p style="font-size: 14px; color: #777;">Or copy and paste this link into your browser:</p>
                        <p style="font-size: 14px; color: #007bff;">%s</p>
                        <p style="font-size: 12px; color: #aaa;">This is an automated message. Please do not reply.</p>
                    </div>
                """.formatted(subject, message, actionUrl, actionUrl);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setFrom(from);
            helper.setText(content, true);
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace(); // This prints the full stack trace to stderr
        }

    }
}
