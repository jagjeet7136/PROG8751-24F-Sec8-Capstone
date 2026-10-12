package com.app.ecommerce.model.response;

public record CartItemResponse(
        Long id,
        CartProductResponse product,
        int quantity
) {}