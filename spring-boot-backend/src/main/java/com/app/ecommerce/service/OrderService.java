package com.app.ecommerce.service;

import com.app.ecommerce.entity.Cart;
import com.app.ecommerce.entity.Order;
import com.app.ecommerce.entity.OrderItem;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.exceptions.ValidationException;
import com.app.ecommerce.model.dto.OrderDTO;
import com.app.ecommerce.repository.CartItemRepository;
import com.app.ecommerce.repository.CartRepository;
import com.app.ecommerce.repository.OrderRepository;
import com.app.ecommerce.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public void createOrder(OrderDTO orderDTO, User user) throws ValidationException {
        Map<String, String> validationErrors = validateOrderDTO(orderDTO);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Order validation failed");
        }

        Order order = new Order();
        order.setUser(user);
        order.setFirstName(orderDTO.getFirstName());
        order.setLastName(orderDTO.getLastName());
        order.setEmail(orderDTO.getEmail());
        order.setPhone(orderDTO.getPhone());
        order.setAddress(orderDTO.getAddress());
        order.setCity(orderDTO.getCity());
        order.setPostalCode(orderDTO.getPostalCode());
        order.setState(orderDTO.getState());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setSubtotal(orderDTO.getSubtotal());
        order.setTax(orderDTO.getTax());
        order.setShippingCharge(orderDTO.getShippingCharge());
        order.setTotal(orderDTO.getTotal());

        Order savedOrder = orderRepository.save(order);
        List<OrderItem> orderItems = new ArrayList<>();
        orderDTO.getCartItems().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItems.add(orderItem);
        });

        orderItemRepository.saveAll(orderItems);

        Cart userCart = cartRepository.findByUser(user).orElse(null);
        if (userCart != null) {
            cartItemRepository.deleteAll(userCart.getItems());
            userCart.getItems().clear();
            cartRepository.save(userCart);
        }
    }

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
}
