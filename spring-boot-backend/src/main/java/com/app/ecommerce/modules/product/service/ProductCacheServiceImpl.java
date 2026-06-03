package com.app.ecommerce.modules.product.service;

import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCacheServiceImpl implements ProductCacheService {

    private static final String PRODUCT_KEY_PREFIX = "product:";
    private static final String ALL_PRODUCTS_KEY = "products:all";
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public ProductResponse get(Long productId) {
        try {
            String json =
                    redisTemplate.opsForValue()
                            .get(PRODUCT_KEY_PREFIX + productId);
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(
                    json,
                    ProductResponse.class
            );

        } catch (Exception e) {
            log.error("Failed to read product from cache", e);
            return null;
        }
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        try {
            String json =
                    redisTemplate.opsForValue()
                            .get(ALL_PRODUCTS_KEY);
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(
                    json,
                    new com.fasterxml.jackson.core.type.TypeReference<List<ProductResponse>>() {
                    }
            );
        } catch (Exception e) {
            log.error("Failed to read product list from cache", e);
            return null;
        }
    }

    @Override
    public void put(Long productId,
                    ProductResponse response,
                    Duration ttl) {
        try {
            String json =
                    objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue()
                    .set(
                            PRODUCT_KEY_PREFIX + productId,
                            json,
                            ttl
                    );
        } catch (Exception e) {
            log.error("Failed to cache product", e);
        }
    }

    @Override
    public void putAllProducts(
            List<ProductResponse> products,
            Duration ttl) {
        try {
            String json =
                    objectMapper.writeValueAsString(products);
            redisTemplate.opsForValue()
                    .set(
                            ALL_PRODUCTS_KEY,
                            json,
                            ttl
                    );
        } catch (Exception e) {
            log.error("Failed to cache product list", e);
        }
    }

    @Override
    public void evict(Long productId) {
        redisTemplate.delete(
                PRODUCT_KEY_PREFIX + productId
        );
    }

    @Override
    public void evictAllProducts() {
        redisTemplate.delete(
                ALL_PRODUCTS_KEY
        );
    }
}