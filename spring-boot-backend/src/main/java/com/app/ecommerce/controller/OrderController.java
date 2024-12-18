package com.app.ecommerce.controller;

import com.app.ecommerce.entity.Order;
import com.app.ecommerce.entity.OrderItem;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.exceptions.ForbiddenException;
import com.app.ecommerce.exceptions.OrderNotFoundException;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.model.dto.OrderDTO;
import com.app.ecommerce.service.OrderService;
import com.app.ecommerce.service.UserService;
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

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<String> createOrder(
            @RequestBody OrderDTO orderDTO,
            Principal principal) throws ValidationException {
        User user = userService.getLoggedInUser(principal);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or user not found");
        }
        orderService.createOrder(orderDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Order created successfully");
    }

    @GetMapping()
    public ResponseEntity<List<Order>> getOrdersForUser(Principal principal) {
        User user = userService.getLoggedInUser(principal);
        return new ResponseEntity<>(orderService.getOrdersByUser(user), HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> getOrderDetails(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderDetails(orderId);
            return ResponseEntity.ok(order);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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
        // Retrieve order and user data from the database (mocked data here)
        Order order = orderService.getOrderDetails(orderId);
        User user = userService.getLoggedInUser(principal);

        if (order.getUser().getId() != user.getId()) {
            throw new ForbiddenException("You are authorized");
        }

        // Create a new PDF document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Logo
        String logoPath = "src/main/resources/static/images/company-logo.png";
        PDImageXObject logoImage = PDImageXObject.createFromFile(logoPath, document);
        contentStream.drawImage(logoImage, 50, 740, 100, 75); // Increased height of the logo

        // Company Information on the far right
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.setNonStrokingColor(105, 105, 105); // Light black color
        contentStream.newLineAtOffset(420, 740); // Position on the right
        contentStream.showText("XYZ Corporation");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("1234 Main St, City, Country");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Phone: +1-800-123-4567");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Email: support@xyzcorp.com");
        contentStream.endText();

        // Customer Information
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.setNonStrokingColor(0, 0, 0); // Black
        contentStream.newLineAtOffset(50, 670);
        contentStream.showText("Customer Information");
        contentStream.newLineAtOffset(0, -20);
        contentStream.setFont(PDType1Font.HELVETICA, 10); // Non-bold customer info
        contentStream.showText("Name: " + order.getFirstName() + " " + order.getLastName());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Email: " + order.getEmail());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Address: " + order.getAddress() + " ," + order.getPostalCode() + " ," + order.getCity());
        contentStream.endText();

        // Order Details on the right
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.setNonStrokingColor(0, 0, 0); // Black
        contentStream.newLineAtOffset(320, 670);
        contentStream.showText("Order Details");
        contentStream.newLineAtOffset(0, -20);
        contentStream.setFont(PDType1Font.HELVETICA, 10); // Non-bold order details
        contentStream.showText("Order ID: " + order.getId());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Order Date: " + order.getOrderDate());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Invoice ID: " + "ECOM" + order.getId());
        contentStream.endText();

        // Solid black horizontal line separating customer and order details
        contentStream.setLineWidth(1f);
        contentStream.moveTo(50, 630);
        contentStream.lineTo(550, 630);
        contentStream.stroke();

        // Ordered Products heading
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.setNonStrokingColor(0, 0, 0); // Black
        contentStream.newLineAtOffset(50, 600);
        contentStream.showText("Ordered Products");
        contentStream.endText();

        // Table for Ordered Products
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
        contentStream.setNonStrokingColor(0, 0, 0); // Black

        // Draw Table Header
        float[] columnWidths = {200f, 100f, 100f};
        for (int i = 0; i < header.length; i++) {
            contentStream.setLineWidth(1f);
            contentStream.moveTo(50 + (i * 150), yPosition);
            contentStream.lineTo(50 + (i * 150) + columnWidths[i], yPosition);
            contentStream.stroke();
        }

        // Draw table rows (Ordered products)
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

        // Draw Total Information
        yPosition -= rowHeight + 20; // Leave space after table
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

        // Terms and Conditions at the bottom right
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.setNonStrokingColor(105, 105, 105); // Light black
        contentStream.newLineAtOffset(420, 100);
        contentStream.showText("1. Item cannot be refunded after 30 days.");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("2. Shipping charges are non-refundable.");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("3. Warranty does not cover misuse or damage.");
        contentStream.endText();

        // End the content stream and close the document
        contentStream.close();

        // Write PDF to a ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        document.close();

        // Set the response headers to indicate PDF content
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + orderId + ".pdf");

        // Return the PDF as a byte array
        return ResponseEntity.ok().headers(headers).body(byteArrayOutputStream.toByteArray());
    }


}
