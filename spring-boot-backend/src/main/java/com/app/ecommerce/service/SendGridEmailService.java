package com.app.ecommerce.service;

import com.app.ecommerce.entity.User;
import com.app.ecommerce.entity.VerificationToken;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.repository.VerificationTokenRepository;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SendGridEmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    public void sendPasswordResetEmail(User user) throws ValidationException {

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        verificationTokenRepository.save(verificationToken);

        String passwordResetLink = "http://localhost:3000/password-reset?token=" + token;

        String emailBody = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "  @import url('https://fonts.googleapis.com/css2?family=Bangers&display=swap');" +
                "  .container { font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; }" +
                "  .header { text-align: center; }" +
                "  .logo-text { font-family: 'Bangers', Impact, sans-serif; font-size: 36px; color: maroon; margin-bottom: 0; }" +
                "  .title { font-family: 'Bangers', Impact, sans-serif; font-size: 28px; margin: 10px 0; }" +
                "  .body { margin: 20px 0; font-size: 16px; }" +
                "  .button {" +
                "    display: inline-block;" +
                "    padding: 10px 20px;" +
                "    background-color: maroon;" +
                "    color: white !important;" +
                "    text-decoration: none !important;" +
                "    border-radius: 4px;" +
                "    font-weight: bold;" +
                "    transition: background-color 0.3s ease;" +
                "  }" +
                "  .button:hover {" +
                "    background-color: rgb(145, 54, 54);" +
                "  }" +
                "  .footer { font-size: 12px; color: #777; margin-top: 30px; text-align: center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "  <div class='header'>" +
                "    <h1 class='logo-text'>SHOPEE</h1>" +
                "    <h2 class='title'>Reset Your Password</h2>" +
                "  </div>" +
                "  <div class='body'>" +
                "    <p>Hi " + user.getUserFullName() + ",</p>" +
                "    <p>We received a request to reset your password. Click the button below to reset it. This link will expire in 1 hour.</p>" +
                "    <p><a href='" + passwordResetLink + "' class='button'>Reset Password</a></p>" +
                "    <p>If the button doesn't work, copy and paste this link into your browser:</p>" +
                "    <p><a href='" + passwordResetLink + "'>" + passwordResetLink + "</a></p>" +
                "    <p>If you didn't request a password reset, please ignore this email.</p>" +
                "  </div>" +
                "  <div class='footer'>" +
                "    <p>&copy; 2025 SHOPEE. All rights reserved.</p>" +
                "  </div>" +
                "</div>" +
                "</body>" +
                "</html>";

        Content content = new Content("text/html", emailBody);
        Email from = new Email("jagjeet7136@gmail.com");
        Email to = new Email(user.getUsername());
        Mail mail = new Mail(from, "Password Reset", to, content);

        sendEmail(user, mail);
    }

    public void sendUserVerificationEmail(User user) throws ValidationException {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationTokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:3000/verify-email?token=" + token;

        String logoUrl = "https://yourdomain.com/logo.png";

        String emailBody = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "  @import url('https://fonts.googleapis.com/css2?family=Bangers&display=swap');" + // May not work in all clients
                "  .container { font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; }" +
                "  .header { text-align: center; }" +
                "  .logo-text { font-family: 'Bangers', Impact, sans-serif; font-size: 36px; color: maroon; margin-bottom: 0; }" +
                "  .title { font-family: 'Bangers', Impact, sans-serif; font-size: 28px; margin: 10px 0; }" +
                "  .body { margin: 20px 0; font-size: 16px; }" +
                "  .button {" +
                "    display: inline-block;" +
                "    padding: 10px 20px;" +
                "    background-color: maroon;" +
                "    color: white;" +
                "    text-decoration: none;" +
                "    border-radius: 4px;" +
                "    transition: background-color 0.3s ease;" +
                "  }" +
                "  .button:hover {" +
                "    background-color: rgb(145, 54, 54);" +
                "  }"+
                "  .footer { font-size: 12px; color: #777; margin-top: 30px; text-align: center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "  <div class='header'>" +
                "    <h1 class='logo-text'>SHOPEE</h1>" +
                "    <h2 class='title'>Verify Your Email</h2>" +
                "  </div>" +
                "  <div class='body'>" +
                "    <p>Hi " + user.getUserFullName() + ",</p>" +
                "    <p>Click the button below to verify your email, this link will be valid for next 24 hours only.</p>" +
                "    <p><a href='"+ verificationLink + "' style='display:inline-block; padding:10px 20px; background-color:maroon; color:white !important; text-decoration:none !important; border-radius:4px; font-weight:bold;'>" + "Verify Email" + "</a></p>" +
                "    <p>If that doesn’t work, paste this link into your browser:</p>" +
                "    <p><a href='" + verificationLink + "'>" + verificationLink + "</a></p>" +
                "  </div>" +
                "  <div class='footer'>" +
                "    <p>&copy; 2025 SHOPEE. All rights reserved.</p>" +
                "  </div>" +
                "</div>" +
                "</body>" +
                "</html>";

        Content content = new Content("text/html", emailBody); // Use HTML content type
        Email from = new Email("jagjeet7136@gmail.com");
        Email to = new Email(user.getUsername());
        Mail mail = new Mail(from, "Email Verification", to, content);

        sendEmail(user, mail);
    }


    public void sendEmail(User user, Mail mail) throws ValidationException {
        try {
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println(response.getBody());
            System.out.println("Email sent: " + response.getStatusCode());
        }
        catch (Exception exc) {
            throw new ValidationException("Some Error Occur while sending email");
        }
    }
}
