package com.app.ecommerce.model.dto;

public interface ProductRatingSummary {

    Long getProductId();

    Double getAverageRating();

    Long getTotalRatings();
}