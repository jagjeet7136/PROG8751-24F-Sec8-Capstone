package com.app.ecommerce.config;

import com.stripe.Stripe;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
public class StripeConfig {
    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.load();
        Stripe.apiKey = dotenv.get("STRIPE_API_SECRET");
    }

    public static String getWebhookSecret() {
        Dotenv dotenv = Dotenv.load();
        return dotenv.get("STRIPE_WEBHOOK_SECRET");
    }
}
