package com.app.ecommerce.modules.product.cache;

import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.fasterxml.jackson.core.type.TypeReference;
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

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public ProductResponse getCachedProduct(Long productId) {
        try {
            String json = redisTemplate.opsForValue().get(ProductCacheKeys.product(productId));
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json, ProductResponse.class);

        } catch (Exception e) {
            log.error("Failed to read product from cache", e);
            return null;
        }
    }

    @Override
    public List<ProductResponse> getCachedProductList() {
        try {
            String json = redisTemplate.opsForValue().get(ProductCacheKeys.ALL_PRODUCTS);
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json,
                    new TypeReference<List<ProductResponse>>() {});
        } catch (Exception e) {
            log.error("Failed to read product list from cache", e);
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
    public void cacheProductList(List<ProductResponse> products, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(products);
            redisTemplate.opsForValue().set(ProductCacheKeys.ALL_PRODUCTS, json, ttl);
        } catch (Exception e) {
            log.error("Failed to cache product list", e);
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
    public void evictProductList() {
        try {
            redisTemplate.delete(ProductCacheKeys.ALL_PRODUCTS);
            log.info("CACHE EVICT - All Products");
        } catch (Exception e) {
            log.error("Failed to evict product list from cache: {}", e.getMessage());
        }
    }
}