package com.app.ecommerce.model.response;

import java.math.BigDecimal;

public record CartProductResponse(
        Long id,
        String name,
        BigDecimal price,
        String imageUrl
) {}