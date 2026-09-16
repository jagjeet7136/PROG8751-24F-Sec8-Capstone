package com.app.ecommerce.modules.product.cache;

public final class ProductCacheKeys {

    private ProductCacheKeys() {}

    public static final String ALL_PRODUCTS = "products:all";

    public static String product(Long productId) {
        return "product:" + productId;
    }
}