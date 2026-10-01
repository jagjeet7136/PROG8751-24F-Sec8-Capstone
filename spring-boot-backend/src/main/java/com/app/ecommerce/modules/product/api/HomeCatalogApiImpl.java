package com.app.ecommerce.modules.product.api;

import com.app.ecommerce.modules.product.cache.ProductCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeCatalogApiImpl implements HomeCatalogApi {

    private final ProductCacheService productCacheService;

    @Override
    public void evictHomeCache() {
        productCacheService.evictHomeProducts();
    }
}