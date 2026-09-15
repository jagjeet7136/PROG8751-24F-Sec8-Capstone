package com.app.ecommerce.modules.product.cache;

public final class ProductCacheKeys {

    private ProductCacheKeys() {}

    public static final String ALL_PRODUCTS = "products:all";

    public static String product(Long productId) {
        return "product:" + productId;
    }

    public static String category(Long categoryId) {
        return "products:category:" + categoryId;
    }

    public static String page(int page, int size, String sortBy, String sortOrder) {
        return String.format(
                "products:page:%d:size:%d:sort:%s:%s",
                page,
                size,
                sortBy,
                sortOrder
        );
    }

    public static String search(String keyword) {
        return "products:search:" + keyword.trim().toLowerCase();
    }
}