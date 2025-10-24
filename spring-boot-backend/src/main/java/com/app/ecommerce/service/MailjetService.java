package com.app.ecommerce.service;

import com.app.ecommerce.entity.User;
import com.app.ecommerce.entity.VerificationToken;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.repository.VerificationTokenRepository;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailjetService {

    @Value("${MAILJET_API_KEY}")
    private String mailjetAPIKey;

    @Value("${MAILJET_SECRET_KEY}")
    private String mailjetSecretKey;

    @Value("${mailjet.sender.email}")
    private String senderEmail;

    @Value("${mailjet.sender.name}")
    private String senderName;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    public void sendUserVerificationEmail(User user) throws ValidationException {
        String token = generateAndSaveToken(user, 24);
        String verificationLink = "http://localhost:3000/verify-email?token=" + token;

        log.info("Generated verification token for user [{}], expires in 24 hours", user.getUsername());

        String emailBody = buildVerificationEmail(user, verificationLink);
        sendEmail(user, "Email Verification", emailBody);
    }

    public void sendPasswordResetEmail(User user) throws ValidationException {
        String token = generateAndSaveToken(user, 1);
        String resetLink = "http://localhost:3000/password-reset?token=" + token;

        log.info("Generated password reset token for user [{}], expires in 1 hour", user.getUsername());

        String emailBody = buildPasswordResetEmail(user, resetLink);
        sendEmail(user, "Password Reset", emailBody);
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

    private String buildVerificationEmail(User user, String link) {
        return getEmailTemplate(user, "Verify Your Email",
                "Click the button below to verify your email. This link will be valid for the next 24 hours only.",
                "Verify Email", link);
    }

    private String buildPasswordResetEmail(User user, String link) {
        return getEmailTemplate(user, "Reset Your Password",
                "We received a request to reset your password. Click the button below to reset it. This link will expire in 1 hour.",
                "Reset Password", link);
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

    public void sendEmail(User user, String subject, String messageBody) {
        try {
            ClientOptions options = ClientOptions.builder().apiKey(mailjetAPIKey).apiSecretKey(mailjetSecretKey).build();
            MailjetClient client = new MailjetClient(options);

            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", senderEmail)
                                            .put("Name", senderName))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", user.getUsername())
                                                    .put("Name", user.getUserFullName())))
                                    .put(Emailv31.Message.SUBJECT, subject)
                                    .put(Emailv31.Message.HTMLPART,
                                            messageBody)));

            MailjetResponse response = client.post(request);

            System.out.println("Email sent successfully!");
            System.out.println("Status: " + response.getStatus());
            System.out.println("Response: " + response.getData().toString());

        } catch (Exception e) {
            log.error("Error sending email to {}: {}", user.getUsername(), e.getMessage(), e);
        }
    }
}
