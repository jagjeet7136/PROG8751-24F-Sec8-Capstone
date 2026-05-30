package com.app.ecommerce.modules.product.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String longDescription;
    private BigDecimal discountedPrice;
    private BigDecimal price;
    private String imageUrl;
    private Integer stock;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double averageRating;
    private Integer totalRatings;
}