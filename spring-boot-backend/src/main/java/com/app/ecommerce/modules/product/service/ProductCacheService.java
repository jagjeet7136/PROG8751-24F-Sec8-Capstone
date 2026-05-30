package com.app.ecommerce.modules.product.service;

import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import java.time.Duration;

public interface ProductCacheService {

    ProductResponse get(Long productId);
    void put(Long productId, ProductResponse response, Duration ttl);
    void evict(Long productId);
}