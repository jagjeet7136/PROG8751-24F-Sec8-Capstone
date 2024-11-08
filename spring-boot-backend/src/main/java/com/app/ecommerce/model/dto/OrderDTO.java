package com.app.ecommerce.model.dto;

import com.app.ecommerce.entity.CartItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String postalCode;
    private String state;
    private String paymentMethod;
    private List<CartItem> cartItems;  // List of cart items from the frontend

    private double subtotal;
    private double tax;
    private double total;
    private double shippingCharge;
}
