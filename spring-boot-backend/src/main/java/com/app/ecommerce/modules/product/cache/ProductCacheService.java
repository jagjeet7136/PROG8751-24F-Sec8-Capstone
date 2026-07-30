package com.app.ecommerce.modules.product.cache;

import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import java.time.Duration;
import java.util.List;

public interface ProductCacheService {

    ProductResponse getCachedProduct(Long productId);
    void cacheProduct(Long productId, ProductResponse response, Duration ttl);
    void evictProduct(Long productId);
    List<ProductResponse> getCachedProductList();
    void cacheProductList(List<ProductResponse> products, Duration ttl);
    void evictProductList();
}