package com.app.ecommerce.modules.product.service;

import com.app.ecommerce.modules.product.dto.response.HomeProductsResponse;

public interface HomeCatalogService {

    HomeProductsResponse getHomeProducts();
}