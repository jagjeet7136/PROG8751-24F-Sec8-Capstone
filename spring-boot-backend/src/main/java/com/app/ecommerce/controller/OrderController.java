package com.app.ecommerce.controller;

import com.app.ecommerce.entity.Order;
import com.app.ecommerce.entity.OrderItem;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.exceptions.ForbiddenException;
import com.app.ecommerce.exceptions.NotFoundException;
import com.app.ecommerce.model.dto.CartItemDTO;
import com.app.ecommerce.model.dto.OrderDTO;
import com.app.ecommerce.service.OrderService;
import com.app.ecommerce.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import com.stripe.param.checkout.SessionCreateParams;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.stripe.model.checkout.Session;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.*;


@RestController
@RequestMapping("/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<List<Order>> getOrdersForUser(Principal principal) {
        User user = userService.getLoggedInUser(principal);
        return new ResponseEntity<>(orderService.getOrdersByUser(user), HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> getOrderDetails(@PathVariable Long orderId) throws NotFoundException {
        try {
            Order order = orderService.getOrderDetails(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            throw new NotFoundException("Order not found");
        }
    }

    @GetMapping("/userOrders/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getOrdersForUser(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/invoice/generate/{orderId}")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long orderId, HttpServletResponse response,
                                                  Principal principal) throws IOException {
        Order order = orderService.getOrderDetails(orderId);
        User user = userService.getLoggedInUser(principal);

        if (!Objects.equals(order.getUser().getId(), user.getId())) {
            throw new ForbiddenException("You are authorized");
        }

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        String logoPath = "src/main/resources/static/images/company-logo.png";
        PDImageXObject logoImage = PDImageXObject.createFromFile(logoPath, document);
        contentStream.drawImage(logoImage, 50, 740, 100, 75);

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.setNonStrokingColor(105, 105, 105);
        contentStream.newLineAtOffset(420, 740);
        contentStream.showText("XYZ Corporation");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("1234 Main St, City, Country");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Phone: +1-800-123-4567");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Email: support@xyzcorp.com");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.setNonStrokingColor(0, 0, 0);
        contentStream.newLineAtOffset(50, 670);
        contentStream.showText("Customer Information");
        contentStream.newLineAtOffset(0, -20);
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.showText("Name: " + order.getFirstName() + " " + order.getLastName());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Email: " + order.getEmail());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Address: " + order.getAddress() + " ," + order.getPostalCode() + " ," + order.getCity());
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.setNonStrokingColor(0, 0, 0); // Black
        contentStream.newLineAtOffset(320, 670);
        contentStream.showText("Order Details");
        contentStream.newLineAtOffset(0, -20);
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.showText("Order ID: " + order.getId());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Order Date: " + order.getOrderDate());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Invoice ID: " + "ECOM" + order.getId());
        contentStream.endText();

        contentStream.setLineWidth(1f);
        contentStream.moveTo(50, 630);
        contentStream.lineTo(550, 630);
        contentStream.stroke();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.setNonStrokingColor(0, 0, 0);
        contentStream.newLineAtOffset(50, 600);
        contentStream.showText("Ordered Products");
        contentStream.endText();

        float yStart = 580;
        float yPosition = yStart;
        float tableWidth = 500;
        float cellMargin = 5f;
        float rowHeight = 20f;
        float cellHeight = rowHeight;
        float tableBottomMargin = 10f;
        String[] header = {"Product", "Quantity", "Price"};

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.setLineWidth(1f);
        contentStream.setNonStrokingColor(0, 0, 0);

        float[] columnWidths = {200f, 100f, 100f};
        for (int i = 0; i < header.length; i++) {
            contentStream.setLineWidth(1f);
            contentStream.moveTo(50 + (i * 150), yPosition);
            contentStream.lineTo(50 + (i * 150) + columnWidths[i], yPosition);
            contentStream.stroke();
        }

        for (OrderItem item : order.getOrderItems()) {
            yPosition -= rowHeight;
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText(item.getProduct().getName());
            contentStream.newLineAtOffset(150, 0);
            contentStream.showText(String.valueOf(item.getQuantity()));
            contentStream.newLineAtOffset(100, 0);
            contentStream.showText("$" + item.getPrice());
            contentStream.endText();
        }

        yPosition -= rowHeight + 20;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Sub Total: $" + order.getSubtotal());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Tax: $" + order.getTax());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Shipping: $" + order.getShippingCharge());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Grand Total: $" + order.getTotal());
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.setNonStrokingColor(105, 105, 105);
        contentStream.newLineAtOffset(420, 100);
        contentStream.showText("1. Item cannot be refunded after 30 days.");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("2. Shipping charges are non-refundable.");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("3. Warranty does not cover misuse or damage.");
        contentStream.endText();
        contentStream.close();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        document.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + orderId + ".pdf");
        return ResponseEntity.ok().headers(headers).body(byteArrayOutputStream.toByteArray());
    }

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody OrderDTO orderRequest,
                                                                     Principal principal) throws StripeException,
            JsonProcessingException {
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        User user = userService.getLoggedInUser(principal);
        if (!user.getId().equals(orderRequest.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "User ID mismatch"));
        }
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
            System.out.println("Creating line item for: " + item.getProductName());
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
        return ResponseEntity.ok(responseData);
    }
}
