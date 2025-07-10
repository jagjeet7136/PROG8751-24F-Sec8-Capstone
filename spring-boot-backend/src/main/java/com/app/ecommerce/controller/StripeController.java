package com.app.ecommerce.controller;

import com.app.ecommerce.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
@Slf4j
public class StripeController {

    private final StripeService stripeService;

    @PostMapping("/save-order-webhook")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload,
                                                    @RequestHeader("Stripe-Signature") String sigHeader) {
        log.info("Received Stripe webhook request [POST /stripe/save-order-webhook]");

        try {
            stripeService.handleStripeWebhook(payload, sigHeader);
            return ResponseEntity.ok("Received");
        } catch (SignatureVerificationException e) {
            log.error("Stripe signature verification failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (StripeException e) {
            log.error("Stripe exception occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Stripe error");
        }
    }
}
