package com.app.ecommerce.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
public class StripeConfig {
    private final String stripeApiKey;

    public StripeConfig(@Value("${stripe.api-key}") String stripeApiKey) {
        this.stripeApiKey = stripeApiKey;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }
}
