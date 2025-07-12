package com.app.ecommerce.service;

import com.app.ecommerce.entity.User;
import com.app.ecommerce.model.dto.CartItemDTO;
import com.app.ecommerce.model.dto.OrderDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeService {

    private final OrderService orderService;

    @Value("${STRIPE_WEBHOOK_KEY}")
    private String stripeWebhookSecret;

    public void handleStripeWebhook(String payload, String sigHeader) throws SignatureVerificationException, StripeException {
        log.info("Stripe Webhook [POST /stripe/save-order-webhook] - Verifying signature");

        Event event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
        log.info("Stripe Webhook event received: {}", event.getType());

        if ("checkout.session.completed".equals(event.getType())) {
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

            if (deserializer.getRawJson() != null) {
                JSONObject raw = new JSONObject(deserializer.getRawJson());
                String sessionId = raw.getString("id");

                log.info("Retrieving Stripe session with ID: {}", sessionId);
                Session session = Session.retrieve(sessionId);

                orderService.saveOrder(session);
                log.info("Order saved for session: {}", sessionId);
            } else {
                log.warn("Raw JSON not available for Stripe checkout.session.completed event");
            }
        } else {
            log.info("Unhandled Stripe event type: {}", event.getType());
        }
    }

    public Map<String, String> createCheckoutSession(User user, OrderDTO orderRequest) throws JsonProcessingException,
            StripeException {
        log.info("Creating Stripe checkout session for user id={}", user.getId());

        if (!user.getId().equals(orderRequest.getUserId())) {
            log.warn("User ID mismatch: logged-in user id={} does not match order user id={}", user.getId(), orderRequest.getUserId());
            throw new IllegalArgumentException("User ID mismatch");
        }

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (CartItemDTO item : orderRequest.getCartItems()) {
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) item.getQuantity())
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("cad")
                                    .setUnitAmount((long) (item.getProductPrice() * 100))
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(item.getProductName())
                                                    .build()
                                    )
                                    .build()
                    ).build();

            log.debug("Added line item to checkout session: product={}, quantity={}", item.getProductName(), item.getQuantity());
            lineItems.add(lineItem);
        }

        lineItems.add(
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("cad")
                                        .setUnitAmount(500L)
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Shipping")
                                                        .build()
                                        )
                                        .build()
                        ).build()
        );

        lineItems.add(
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("cad")
                                        .setUnitAmount(1300L)
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Tax (13%)")
                                                        .build()
                                        )
                                        .build()
                        ).build()
        );

        Map<String, String> metadata = new HashMap<>();
        metadata.put("firstName", orderRequest.getFirstName());
        metadata.put("lastName", orderRequest.getLastName());
        metadata.put("phone", orderRequest.getPhone());
        metadata.put("address", orderRequest.getAddress());
        metadata.put("city", orderRequest.getCity());
        metadata.put("postalCode", orderRequest.getPostalCode());
        metadata.put("state", orderRequest.getState());
        metadata.put("userId", String.valueOf(orderRequest.getUserId()));
        metadata.put("subtotal", String.valueOf(orderRequest.getSubtotal()));
        metadata.put("tax", String.valueOf(orderRequest.getTax()));
        metadata.put("shipping", String.valueOf(orderRequest.getShippingCharge()));
        metadata.put("products", new ObjectMapper().writeValueAsString(orderRequest.getCartItems()));

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/success")
                .setCancelUrl("http://localhost:3000/cancel")
                .putAllMetadata(metadata)
                .addAllPaymentMethodType(List.of(SessionCreateParams.PaymentMethodType.CARD))
                .addAllLineItem(lineItems)
                .build();

        Session session = Session.create(params);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("url", session.getUrl());

        log.info("Stripe checkout session created successfully for user id={}, session id={}", user.getId(), session.getId());
        return responseData;
    }
}
