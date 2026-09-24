package com.app.ecommerce.modules.product.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HomeProductsResponse {

    private List<ProductResponse> newlyReleased;

    private List<ProductResponse> topSelling;

    private List<ProductResponse> exclusiveDeals;

    private List<ProductResponse> topRated;
}