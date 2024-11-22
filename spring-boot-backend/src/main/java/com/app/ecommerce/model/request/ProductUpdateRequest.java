package com.app.ecommerce.model.request;

import lombok.Data;

@Data
public class ProductUpdateRequest {

    private String name;

    private String description;

    private String longDescription;

    private Double discountedPrice;

    private Double price;

    private String imageUrl;

    private Integer stock;

    private String categoryName;
}
