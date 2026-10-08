package com.app.ecommerce.modules.product.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeProductsResponse {

    private List<ProductResponse> newlyReleased;

    private List<ProductResponse> topSelling;

    private List<ProductResponse> exclusiveDeals;

    private List<ProductResponse> topRated;
}