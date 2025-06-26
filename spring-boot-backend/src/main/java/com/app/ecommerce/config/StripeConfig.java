package com.app.ecommerce.config;

import com.stripe.Stripe;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
public class StripeConfig {
    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.load();
        Stripe.apiKey = dotenv.get("STRIPE_API_KEY");
    }
}
