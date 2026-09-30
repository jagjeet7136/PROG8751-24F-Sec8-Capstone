package com.app.ecommerce.modules.product.cache;

import com.app.ecommerce.modules.product.dto.response.HomeProductsResponse;
import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCacheServiceImpl implements ProductCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public ProductResponse getCachedProduct(Long productId) {
        try {
            String json = redisTemplate.opsForValue().get(ProductCacheKeys.product(productId));
            if (json == null) {
                return null;
            }
            log.info("CACHE HIT - Product {}", productId);
            return objectMapper.readValue(json, ProductResponse.class);

        } catch (Exception e) {
            log.error("Failed to read product from cache", e);
            return null;
        }
    }

    @Override
    public void cacheProduct(Long productId, ProductResponse response, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue()
                    .set(ProductCacheKeys.product(productId), json, ttl);
            log.info("CACHE PUT - Product {}", productId);
        } catch (Exception e) {
            log.error("Failed to cache product: {}", e.getMessage());
        }
    }

    @Override
    public void evictProduct(Long productId) {
        try {
            redisTemplate.delete(ProductCacheKeys.product(productId));
            log.info("CACHE EVICT - Product {}", productId);
        } catch (Exception e) {
            log.error("Failed to evict product {} from cache: {}", productId, e.getMessage());
        }
    }

    @Override
    public HomeProductsResponse getCachedHomeProducts() {

        try {
            String json =
                    redisTemplate.opsForValue()
                            .get(ProductCacheKeys.HOME_PRODUCTS);

            if (json == null) {
                return null;
            }

            log.debug("CACHE HIT: {}", ProductCacheKeys.HOME_PRODUCTS);

            return objectMapper.readValue(
                    json,
                    HomeProductsResponse.class
            );

        } catch (Exception e) {
            log.warn(
                    "Failed to read homepage products from cache: {}",
                    e.getMessage()
            );

            return null;
        }
    }

    @Override
    public void cacheHomeProducts(
            HomeProductsResponse response,
            Duration ttl
    ) {

        try {
            String json =
                    objectMapper.writeValueAsString(response);

            redisTemplate.opsForValue().set(
                    ProductCacheKeys.HOME_PRODUCTS,
                    json,
                    ttl
            );

            log.debug(
                    "CACHE PUT: {}",
                    ProductCacheKeys.HOME_PRODUCTS
            );

        } catch (Exception e) {
            log.warn(
                    "Failed to cache homepage products: {}",
                    e.getMessage()
            );
        }
    }

    @Override
    public void evictHomeProducts() {

        try {
            redisTemplate.delete(
                    ProductCacheKeys.HOME_PRODUCTS
            );

            log.debug(
                    "CACHE EVICT: {}",
                    ProductCacheKeys.HOME_PRODUCTS
            );

        } catch (Exception e) {
            log.warn(
                    "Failed to evict homepage products cache: {}",
                    e.getMessage()
            );
        }
    }
}