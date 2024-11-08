package com.app.ecommerce.service;


import com.app.ecommerce.entity.Cart;
import com.app.ecommerce.entity.Order;
import com.app.ecommerce.entity.OrderItem;
import com.app.ecommerce.entity.User;
import com.app.ecommerce.model.dto.OrderDTO;
import com.app.ecommerce.repository.CartItemRepository;
import com.app.ecommerce.repository.CartRepository;
import com.app.ecommerce.repository.OrderRepository;
import com.app.ecommerce.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    public void createOrder(OrderDTO orderDTO, User user) {
        // Create the order
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

        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Create and save order items
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
            cartRepository.save(userCart); // Optionally save the cart to persist the empty list
        }
    }
}
