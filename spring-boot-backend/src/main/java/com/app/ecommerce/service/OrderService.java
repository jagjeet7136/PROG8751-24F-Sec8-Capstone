package com.app.ecommerce.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.app.ecommerce.entity.*;
import com.app.ecommerce.exceptions.ForbiddenException;
import com.app.ecommerce.model.dto.CartItemDTO;
import com.app.ecommerce.model.dto.OrderDTO;
import com.app.ecommerce.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Order> getOrdersByUser(User user) {
        log.info("Fetching orders for user id={}", user.getId());
        return orderRepository.findAllByUser(user);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        log.info("Fetching orders for user id={}", userId);
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderDetails(Long orderId) {
        log.info("Fetching order details for order id={}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
    }

    public void saveOrder(Session session) {
        log.info("Saving order from Stripe session id={}", session.getId());

        String email = session.getCustomerDetails() != null ? session.getCustomerDetails().getEmail() : null;
        String name = session.getCustomerDetails() != null ? session.getCustomerDetails().getName() : null;

        String firstName = null;
        String lastName = null;
        if (name != null && name.contains(" ")) {
            String[] nameParts = name.split(" ", 2);
            firstName = nameParts[0];
            lastName = nameParts.length > 1 ? nameParts[1] : "";
        } else {
            firstName = name != null ? name : "Unknown";
            lastName = "";
        }

        Long userId = Long.parseLong(session.getMetadata().get("userId"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Order order = new Order();
        order.setUser(user);
        order.setFirstName(session.getMetadata().getOrDefault("firstName", firstName));
        order.setLastName(session.getMetadata().getOrDefault("lastName", lastName));
        order.setEmail(email);
        order.setPhone(session.getMetadata().get("phone"));
        order.setAddress(session.getMetadata().get("address"));
        order.setCity(session.getMetadata().get("city"));
        order.setPostalCode(session.getMetadata().get("postalCode"));
        order.setState(session.getMetadata().get("state"));
        order.setPaymentMethod("card");

        double subtotal = Double.parseDouble(session.getMetadata().get("subtotal"));
        double tax = Double.parseDouble(session.getMetadata().get("tax"));
        double shipping = Double.parseDouble(session.getMetadata().get("shipping"));
        double total = session.getAmountTotal() / 100.0;

        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setShippingCharge(shipping);
        order.setTotal(total);

        List<OrderItem> orderItems = new ArrayList<>();
        String productsJson = session.getMetadata().get("products");

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> productList = mapper.readValue(productsJson, new TypeReference<>() {});
            for (Map<String, Object> p : productList) {
                Long productId = Long.valueOf(p.get("productId").toString());
                int quantity = Integer.parseInt(p.get("quantity").toString());
                double price = Double.parseDouble(p.get("productPrice").toString());

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(price);

                orderItems.add(orderItem);
            }
        } catch (Exception e) {
            log.error("Error parsing product metadata from Stripe session", e);
            throw new RuntimeException("Error parsing product metadata", e);
        }

        order.setOrderItems(orderItems);
        orderRepository.save(order);
        log.info("Order saved successfully with id={}", order.getId());
    }

    public byte[] createOrderInvoice(User user, Order order) throws IOException {
        log.info("Generating invoice PDF for order id={} for user id={}", order.getId(), user.getId());

        if (!Objects.equals(order.getUser().getId(), user.getId())) {
            log.warn("User id={} unauthorized to generate invoice for order id={}", user.getId(), order.getId());
            throw new ForbiddenException("You are not authorized");
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
        float rowHeight = 20f;
        String[] header = {"Product", "Quantity", "Price"};

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        float[] columnWidths = {200f, 100f, 100f};
        for (int i = 0; i < header.length; i++) {
            contentStream.moveTo(50 + (i * columnWidths[i]), yPosition);
            contentStream.lineTo(50 + (i * columnWidths[i]) + columnWidths[i], yPosition);
            contentStream.stroke();
        }

        for (OrderItem item : order.getOrderItems()) {
            yPosition -= rowHeight;
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText(item.getProduct().getName());
            contentStream.newLineAtOffset(200, 0);
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

        log.info("Invoice PDF generated successfully for order id={}", order.getId());
        return byteArrayOutputStream.toByteArray();
    }
}
