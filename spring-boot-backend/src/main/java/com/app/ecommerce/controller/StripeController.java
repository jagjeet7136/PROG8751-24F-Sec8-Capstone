package com.app.ecommerce.controller;

import com.app.ecommerce.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("stripe")
public class StripeController {

    @Value("${STRIPE_WEBHOOK_KEY}")
    private String stripeWebhookSecret;

    @Autowired
    private OrderService orderService;

    @PostMapping("/save-order-webhook")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload,
                                                    @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
            System.out.println("Received event type: " + event.getType());
            System.out.println("Raw payload: " + payload);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

            if (deserializer.getRawJson() != null) {
                JSONObject raw = new JSONObject(deserializer.getRawJson());
                String sessionId = raw.getString("id");

                try {
                    Session session = Session.retrieve(sessionId);
                    orderService.saveOrder(session);
                } catch (StripeException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Raw JSON not available to extract session ID");
            }
        }

        return ResponseEntity.ok("Received");
    }

}

