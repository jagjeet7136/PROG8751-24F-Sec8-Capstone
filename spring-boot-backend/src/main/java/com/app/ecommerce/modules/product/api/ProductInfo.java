package com.app.ecommerce.modules.product.api;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class ProductInfo {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
}
