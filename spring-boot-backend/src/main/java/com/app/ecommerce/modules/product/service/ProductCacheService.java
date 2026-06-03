package com.app.ecommerce.modules.product.service;

import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import java.time.Duration;
import java.util.List;

public interface ProductCacheService {

    ProductResponse get(Long productId);
    void put(Long productId, ProductResponse response, Duration ttl);
    void evict(Long productId);
    List<ProductResponse> getAllProducts();
    void putAllProducts(List<ProductResponse> products, Duration ttl);
    void evictAllProducts();
}