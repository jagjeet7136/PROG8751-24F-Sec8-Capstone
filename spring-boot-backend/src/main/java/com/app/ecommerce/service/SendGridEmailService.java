package com.app.ecommerce.service;

import com.app.ecommerce.entity.User;
import com.app.ecommerce.entity.VerificationToken;
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
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SendGridEmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    public void sendUserVerificationEmail(User user) throws IOException {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24-hour validity
        verificationTokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:3000/verify-email?token=" + token;
        String emailBody = "Click to verify your email: " + verificationLink;
        Content content = new Content("text/plain", emailBody);
        Email from = new Email("jagjeet7136@gmail.com");
        Email to = new Email(user.getUsername());
        Mail mail = new Mail(from, "Email Verification", to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);
        System.out.println(response.getBody());
        System.out.println("Email sent: " + response.getStatusCode());
    }
}
