package com.app.ecommerce.model.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long productId;
    private String productName;
    private double productPrice;
    private String productImageUrl;
    private int quantity;
}
