package com.app.ecommerce.service;

import com.app.ecommerce.entity.User;
import com.app.ecommerce.entity.VerificationToken;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.repository.VerificationTokenRepository;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendGridEmailService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    public void sendPasswordResetEmail(User user) throws ValidationException {
        String token = generateAndSaveToken(user, 1);
        String resetLink = "http://localhost:3000/password-reset?token=" + token;

        log.info("Generated password reset token for user [{}], expires in 1 hour", user.getUsername());

        String emailBody = buildPasswordResetEmail(user, resetLink);
        sendEmail(user, "Password Reset", emailBody);
    }

    public void sendUserVerificationEmail(User user) throws ValidationException {
        String token = generateAndSaveToken(user, 24);
        String verificationLink = "http://localhost:3000/verify-email?token=" + token;

        log.info("Generated verification token for user [{}], expires in 24 hours", user.getUsername());

        String emailBody = buildVerificationEmail(user, verificationLink);
        sendEmail(user, "Email Verification", emailBody);
    }

    private String generateAndSaveToken(User user, int expiryHours) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(expiryHours));
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    private void sendEmail(User user, String subject, String emailBody) throws ValidationException {
        try {
            Email from = new Email("jagjeet7136@gmail.com");
            Email to = new Email(user.getUsername());
            Content content = new Content("text/html", emailBody);
            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            log.info("Email sent to [{}], statusCode: {}, body: {}", user.getUsername(), response.getStatusCode(), response.getBody());
        } catch (Exception e) {
            log.error("Failed to send email to [{}]: {}", user.getUsername(), e.getMessage());
            throw new ValidationException("Error occurred while sending email.");
        }
    }

    private String buildPasswordResetEmail(User user, String link) {
        return getEmailTemplate(user, "Reset Your Password",
                "We received a request to reset your password. Click the button below to reset it. This link will expire in 1 hour.",
                "Reset Password", link);
    }

    private String buildVerificationEmail(User user, String link) {
        return getEmailTemplate(user, "Verify Your Email",
                "Click the button below to verify your email. This link will be valid for the next 24 hours only.",
                "Verify Email", link);
    }

    private String getEmailTemplate(User user, String title, String message, String buttonText, String link) {
        return "<!DOCTYPE html>" +
                "<html><head><style>" +
                "  @import url('https://fonts.googleapis.com/css2?family=Bangers&display=swap');" +
                "  .container { font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; }" +
                "  .header { text-align: center; }" +
                "  .logo-text { font-family: 'Bangers', Impact, sans-serif; font-size: 36px; color: maroon; margin-bottom: 0; }" +
                "  .title { font-family: 'Bangers', Impact, sans-serif; font-size: 28px; margin: 10px 0; }" +
                "  .body { margin: 20px 0; font-size: 16px; }" +
                "  .button { display: inline-block; padding: 10px 20px; background-color: maroon; color: white !important; text-decoration: none !important; border-radius: 4px; font-weight: bold; transition: background-color 0.3s ease; }" +
                "  .button:hover { background-color: rgb(145, 54, 54); }" +
                "  .footer { font-size: 12px; color: #777; margin-top: 30px; text-align: center; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "  <div class='header'>" +
                "    <h1 class='logo-text'>SHOPEE</h1>" +
                "    <h2 class='title'>" + title + "</h2>" +
                "  </div>" +
                "  <div class='body'>" +
                "    <p>Hi " + user.getUserFullName() + ",</p>" +
                "    <p>" + message + "</p>" +
                "    <p><a href='" + link + "' class='button'>" + buttonText + "</a></p>" +
                "    <p>If the button doesn't work, copy and paste this link into your browser:</p>" +
                "    <p><a href='" + link + "'>" + link + "</a></p>" +
                "  </div>" +
                "  <div class='footer'>" +
                "    <p>&copy; 2025 SHOPEE. All rights reserved.</p>" +
                "  </div>" +
                "</div></body></html>";
    }
}
