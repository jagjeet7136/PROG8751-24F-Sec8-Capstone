package com.app.ecommerce.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.app.ecommerce.entity.*;
import com.app.ecommerce.model.dto.OrderDTO;
import com.app.ecommerce.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
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

//    @Transactional
//    public void createOrder(OrderDTO orderDTO, User user) throws ValidationException {
//        Map<String, String> validationErrors = validateOrderDTO(orderDTO);
//
//        if (!validationErrors.isEmpty()) {
//            throw new ValidationException("Order validation failed");
//        }
//
//        Order order = new Order();
//        order.setUser(user);
//        order.setFirstName(orderDTO.getFirstName());
//        order.setLastName(orderDTO.getLastName());
//        order.setEmail(orderDTO.getEmail());
//        order.setPhone(orderDTO.getPhone());
//        order.setAddress(orderDTO.getAddress());
//        order.setCity(orderDTO.getCity());
//        order.setPostalCode(orderDTO.getPostalCode());
//        order.setState(orderDTO.getState());
//        order.setPaymentMethod(orderDTO.getPaymentMethod());
//        order.setSubtotal(orderDTO.getSubtotal());
//        order.setTax(orderDTO.getTax());
//        order.setShippingCharge(orderDTO.getShippingCharge());
//        order.setTotal(orderDTO.getTotal());
//
//        Order savedOrder = orderRepository.save(order);
//        List<OrderItem> orderItems = new ArrayList<>();
//        orderDTO.getCartItems().forEach(cartItem -> {
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(savedOrder);
//            orderItem.setProduct(cartItem.getProduct());
//            orderItem.setQuantity(cartItem.getQuantity());
//            orderItem.setPrice(cartItem.getProduct().getPrice());
//            orderItems.add(orderItem);
//        });
//
//        orderItemRepository.saveAll(orderItems);
//
//        Cart userCart = cartRepository.findByUser(user).orElse(null);
//        if (userCart != null) {
//            cartItemRepository.deleteAll(userCart.getItems());
//            userCart.getItems().clear();
//            cartRepository.save(userCart);
//        }
//    }

    private Map<String, String> validateOrderDTO(OrderDTO orderDTO) {
        Map<String, String> errors = new HashMap<>();

        if (!StringUtils.hasText(orderDTO.getFirstName())) {
            errors.put("firstName", "First name is required");
        }

        if (!isValidEmail(orderDTO.getEmail())) {
            errors.put("email", "Invalid email");
        }

        if (!isValidPhone(orderDTO.getPhone())) {
            errors.put("phone", "Phone must be 10 digits");
        }

        if (!StringUtils.hasText(orderDTO.getAddress())) {
            errors.put("address", "Street address is required");
        }

        if (!StringUtils.hasText(orderDTO.getCity())) {
            errors.put("city", "City is required");
        }

        if (!isValidPostalCode(orderDTO.getPostalCode())) {
            errors.put("postalCode", "Invalid postal code format");
        }

        return errors;
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email);
    }

    private boolean isValidPhone(String phone) {
        return Pattern.matches("^\\d{10}$", phone);
    }

    private boolean isValidPostalCode(String postalCode) {
        return Pattern.matches("^[A-Za-z]\\d[A-Za-z]\\d[A-Za-z]\\d$", postalCode);
    }

    private boolean isValidCardNumber(String cardNumber) {
        return Pattern.matches("^\\d{16}$", cardNumber);
    }

    private boolean isValidCVV(String cvv) {
        return Pattern.matches("^\\d{3}$", cvv);
    }

    private boolean isFutureExpiryDate(String expiryDate) {
        Pattern pattern = Pattern.compile("^(0[1-9]|1[0-2])/?([0-9]{2})$");
        java.util.regex.Matcher matcher = pattern.matcher(expiryDate);
        if (!matcher.matches()) {
            return false;
        }
        int month = Integer.parseInt(matcher.group(1));
        int year = Integer.parseInt("20" + matcher.group(2));

        java.util.Calendar today = java.util.Calendar.getInstance();
        java.util.Calendar expiry = java.util.Calendar.getInstance();
        expiry.set(java.util.Calendar.YEAR, year);
        expiry.set(java.util.Calendar.MONTH, month - 1);
        expiry.set(java.util.Calendar.DAY_OF_MONTH, expiry.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));

        return expiry.after(today);
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findAllByUser(user);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
    }

    public void saveOrder(Session session) {
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
                .orElseThrow(() -> new RuntimeException("User not found"));

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
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(price);

                orderItems.add(orderItem);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing product metadata", e);
        }

        order.setOrderItems(orderItems);
        orderRepository.save(order);
    }


}
