package com.app.ecommerce.modules.product.cache;

public final class ProductCacheKeys {

    private ProductCacheKeys() {
    }

    public static final String HOME_PRODUCTS = "products:home";

    public static String product(Long productId) {
        return "product:" + productId;
    }
}