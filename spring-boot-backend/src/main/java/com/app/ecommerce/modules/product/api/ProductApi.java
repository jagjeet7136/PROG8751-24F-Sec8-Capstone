package com.app.ecommerce.modules.product.api;

public interface ProductApi {
        ProductInfo getProductInfo(Long productId);
        void reduceStock(Long productId, int quantity);
}

