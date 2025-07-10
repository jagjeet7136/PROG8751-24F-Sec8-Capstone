package com.app.ecommerce.controller;

import com.app.ecommerce.entity.Order;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.model.dto.OrderDTO;
import com.app.ecommerce.service.OrderService;
import com.app.ecommerce.service.StripeService;
import com.app.ecommerce.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private StripeService stripeService;

    @GetMapping
    public ResponseEntity<List<Order>> getOrdersForUser(Principal principal) {
        log.info("GET /orders - Getting orders for logged-in user");
        User user = userService.getLoggedInUser(principal);
        List<Order> orders = orderService.getOrdersByUser(user);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> getOrderDetails(@PathVariable Long orderId) {
        log.info("GET /orders/{} - Getting order details for admin", orderId);
        Order order = orderService.getOrderDetails(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/userOrders/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getOrdersForUserByAdmin(@PathVariable Long userId) {
        log.info("GET /orders/userOrders/{} - Getting orders for user by admin", userId);
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/invoice/generate/{orderId}")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long orderId, HttpServletResponse response, Principal principal)
            throws IOException {
        log.info("GET /orders/invoice/generate/{} - Generating invoice for order", orderId);
        User user = userService.getLoggedInUser(principal);
        Order order = orderService.getOrderDetails(orderId);
        byte[] invoiceBytes = orderService.createOrderInvoice(user, order);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + orderId + ".pdf");
        return ResponseEntity.ok().headers(headers).body(invoiceBytes);
    }

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody OrderDTO orderRequest, Principal principal)
            throws StripeException, JsonProcessingException {
        log.info("POST /orders/create-checkout-session - Creating checkout session for userId: {}", orderRequest.getUserId());
        User user = userService.getLoggedInUser(principal);
        Map<String, String> sessionData = stripeService.createCheckoutSession(user, orderRequest);
        return ResponseEntity.ok(sessionData);
    }
}
