package com.app.ecommerce.modules.product.cache;

import com.app.ecommerce.modules.product.dto.response.HomeProductsResponse;
import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import java.time.Duration;

public interface ProductCacheService {

    ProductResponse getCachedProduct(Long productId);

    void cacheProduct(
            Long productId,
            ProductResponse response,
            Duration ttl
    );

    void evictProduct(Long productId);

    HomeProductsResponse getCachedHomeProducts();

    void cacheHomeProducts(
            HomeProductsResponse response,
            Duration ttl
    );

    void evictHomeProducts();
}