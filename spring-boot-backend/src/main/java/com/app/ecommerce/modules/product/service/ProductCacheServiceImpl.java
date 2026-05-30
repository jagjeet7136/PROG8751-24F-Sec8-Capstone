package com.app.ecommerce.modules.product.service;

import com.app.ecommerce.modules.product.dto.response.ProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ProductCacheServiceImpl implements ProductCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private String key(Long id) {
        return "product:" + id;
    }

    @Override
    public ProductResponse get(Long productId) {
        Object cached = redisTemplate.opsForValue().get(key(productId));

        if (cached != null) {
            return objectMapper.convertValue(cached, ProductResponse.class);
        };
        return null;
    }

    @Override
    public void put(Long productId, ProductResponse response, Duration ttl) {
        redisTemplate.opsForValue().set(
                key(productId),
                response,
                ttl
        );
    }

    @Override
    public void evict(Long productId) {
        redisTemplate.delete(key(productId));
    }
}